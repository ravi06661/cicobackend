package com.cico.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cico.exception.ResourceNotFoundException;
import com.cico.model.Assignment;
import com.cico.model.AssignmentSubmission;
import com.cico.model.AssignmentTaskQuestion;
import com.cico.model.Course;
import com.cico.payload.AssignmentRequest;
import com.cico.payload.AssignmentSubmissionRequest;
import com.cico.payload.SubmissionAssignmentTaskStatus;
import com.cico.repository.AssignmentRepository;
import com.cico.repository.AssignmentSubmissionRepository;
import com.cico.repository.AssignmentTaskQuestionRepository;
import com.cico.repository.CourseRepository;
import com.cico.repository.StudentRepository;
import com.cico.repository.SubjectRepository;
import com.cico.service.IAssignmentService;
import com.cico.util.AppConstants;
import com.cico.util.SubmissionStatus;

@Service
public class AssignmentServiceImpl implements IAssignmentService {

	@Autowired
	private AssignmentRepository assignmentRepository;

	@Autowired
	private CourseRepository courseRepo;

	@Autowired
	private SubjectRepository subjectRepo;

	@Value("${questionImages}")
	private String QUESTION_IMAGES_DIR;

	@Value("${attachmentFiles}")
	private String ATTACHMENT_FILES_DIR;
  
	@Autowired
	private FileServiceImpl fileServiceImpl;

	@Autowired
	private AssignmentTaskQuestionRepository assignmentTaskQuestionRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private AssignmentSubmissionRepository submissionRepository;

	@Override
	public Assignment getAssignment(Long id) {
		Assignment assignment = assignmentRepository.findByIdAndIsDeleted(id,false)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
		assignment.setAssignmentQuestion(assignment.getAssignmentQuestion().parallelStream().filter(obj1->!obj1.getIsDeleted()).collect(Collectors.toList()));
		return assignment;
	}
	
	@Override
	public ResponseEntity<?> createAssignment(AssignmentRequest assignmentRequest) throws Exception {
	  
		 Optional<Assignment> obj = assignmentRepository.findByName(assignmentRequest.getTitle().trim());
		if(obj.isEmpty()) {
			Assignment assignment = new Assignment();
			assignment.setTitle(assignmentRequest.getTitle());

			assignment.setCourse(courseRepo.findById(assignmentRequest.getCourseId()).get());

			if (assignmentRequest.getSubjectId() != null)
				assignment.setSubject(subjectRepo.findById(assignmentRequest.getSubjectId()).get());

			assignment.setCreatedDate(LocalDateTime.now());
			assignment.setIsDeleted(false);
			Assignment savedAssignment = assignmentRepository.save(assignment);
			savedAssignment = getAssignmentTemp(savedAssignment);
			return new ResponseEntity<>(savedAssignment, HttpStatus.CREATED);
		}else {
			throw new Exception("Assignmnet Already Present With This Title");
		}
	}

	@Override
	public ResponseEntity<?> getAllAssignments() {
		List<Assignment> assignments = assignmentRepository.findByIsDeletedFalse();
		assignments.forEach(obj -> {
			 obj.setAssignmentQuestion(obj.getAssignmentQuestion().parallelStream().filter(obj1->!obj1.getIsDeleted()).collect(Collectors.toList()));
		});
		return new ResponseEntity<>(assignments, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAssignmentQuesById(Long questionId, Long assignmentId) {
		Map<String, Object> response = new HashMap<>();
		AssignmentTaskQuestion assignmentTaskQuestion = assignmentTaskQuestionRepository.findByQuestionId(questionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.NO_DATA_FOUND));
		Assignment assignment = assignmentRepository.findByIdAndIsDeleted(assignmentId, false).get();
		response.put("question", assignmentTaskQuestion);
		response.put("attachment", assignment.getTaskAttachment());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> submitAssignment(MultipartFile file, AssignmentSubmissionRequest readValue) throws Exception {
		AssignmentSubmission obj = submissionRepository.findByAssignmentIdAndQuestionIdAndStudentId(readValue.getAssignmentId(),readValue.getTaskId(),readValue.getStudentId());
//	 if(Objects.nonNull(obj)  && obj.getStatus().name().equals("Rejected")  || !Objects.nonNull(obj)) {
		 if(Objects.isNull(obj)) {
		 AssignmentSubmission submission = new AssignmentSubmission();
			submission.setStudent(studentRepository.findByStudentId(readValue.getStudentId()));
			submission.setAssignmentId(readValue.getAssignmentId());
			submission.setTaskId(readValue.getTaskId());
			submission.setDescription(readValue.getDescription());
			submission.setSubmissionDate(LocalDateTime.now());
			submission.setStatus(SubmissionStatus.Unreviewed);
			if (Objects.nonNull(file)) {
				String fileName = fileServiceImpl.uploadFileInFolder(file, QUESTION_IMAGES_DIR);
				submission.setSubmitFile(fileName);
			}
			submissionRepository.save(submission);
			return new ResponseEntity<>( HttpStatus.CREATED);
	 }else {
		  throw new Exception("ALREADY THIS ASSIGNMENT TASK SUBMITED!!");
	 }
	}

	@Override
	public ResponseEntity<?> getSubmitedAssignmetByStudentId(Integer studentId) {
		List<AssignmentSubmission> list = new ArrayList<>();
		List<Object[]> submitAssignmentByStudentId = submissionRepository.getSubmitAssignmentByStudentId(studentId);
		submitAssignmentByStudentId.forEach(obj -> {
			AssignmentSubmission submission = new AssignmentSubmission();
			submission.setReview((String) obj[0]);
			submission.setStatus((SubmissionStatus) obj[1]);
			submission.setSubmissionDate((LocalDateTime) obj[2]);
			submission.setSubmitFile((String) obj[3]);
			submission.setDescription((String) obj[4]);
			submission.setTaskId((Long) obj[5]);
			submission.setAssignmentId((Long) obj[6]);
			submission.setTitle((String) obj[7]);
			list.add(submission);
		});
		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAllSubmitedAssginments() {
		List<AssignmentSubmission> obj = submissionRepository.findAll();
		return new ResponseEntity<>(obj, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> updateSubmitedAssignmentStatus(Long submissionId, String status, String review) {
		if (status.equals(SubmissionStatus.Reviewing.toString())) {
			submissionRepository.updateSubmitAssignmentStatus(submissionId, SubmissionStatus.Reviewing, review);
		} else if (status.equals(SubmissionStatus.Accepted.toString())) {
			submissionRepository.updateSubmitAssignmentStatus(submissionId, SubmissionStatus.Accepted, review);
		} else if (status.equals(SubmissionStatus.Rejected.toString())) {
			submissionRepository.updateSubmitAssignmentStatus(submissionId, SubmissionStatus.Rejected, review);
		}
		return new ResponseEntity<>(submissionRepository.findById(submissionId).get(), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> addQuestionInAssignment2(String question, String videoUrl,
			List<MultipartFile> questionImages, Long assignmentId) {

		Optional<Assignment> assignmentOptional = assignmentRepository.findByIdAndIsDeleted(assignmentId, false);

		if (assignmentOptional.isPresent()) {
			Assignment assignment = assignmentOptional.get();

			AssignmentTaskQuestion assignmentTaskQuestion = new AssignmentTaskQuestion();
			assignmentTaskQuestion.setQuestion(question);
			assignmentTaskQuestion.setVideoUrl(videoUrl);
			assignmentTaskQuestion.setIsDeleted(false);
			//assignmentTaskQuestion.setAssignmentId(assignmentId);
			List<String> fileNames = questionImages.stream()
					.map(file -> fileServiceImpl.uploadFileInFolder(file, QUESTION_IMAGES_DIR))
					.collect(Collectors.toList());

			assignmentTaskQuestion.setQuestionImages(fileNames);

			assignment.getAssignmentQuestion().add(assignmentTaskQuestion);
           
			Assignment updatedAssignment = assignmentRepository.save(assignment);
			//updatedAssignment = getAssignmentTemp(updatedAssignment);
			return new ResponseEntity<>(updatedAssignment, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}
	}
	@Override
	public ResponseEntity<?> addAssignment(Long assignmentId, MultipartFile attachment) {

		Assignment assignment = assignmentRepository.findById(assignmentId).get();
		if (Objects.nonNull(assignment)) {
			String fileName = fileServiceImpl.uploadFileInFolder(attachment, ATTACHMENT_FILES_DIR);
			assignment.setTaskAttachment(fileName);
		}

		assignmentRepository.save(assignment);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatus() {

		List<Assignment> assignments = assignmentRepository.findByIsDeletedFalse();
		assignments =AllAssignmentTemp(assignments);
		List<SubmissionAssignmentTaskStatus> assignmentTaskStatusList = new ArrayList<>();

		assignments.forEach(assignment -> {
			int taskCount = 0;
			for (AssignmentTaskQuestion q :assignment.getAssignmentQuestion()) {
				int totalSubmitted = 0;
				int underReviewed = 0;
				int reviewed = 0;

				SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
				assignmentTaskStatus.setAssignmentId(assignment.getId());

				List<AssignmentSubmission> submissionAssignments = submissionRepository
						.getSubmitAssignmentByAssignmentId(assignment.getId(), q.getQuestionId());
				totalSubmitted += submissionAssignments.size();
				for (AssignmentSubmission submission : submissionAssignments) {
					if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
						underReviewed += 1;
					} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
							|| submission.getStatus().equals(SubmissionStatus.Accepted)
							|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
						reviewed += 1;
					}
				}
				assignmentTaskStatus.setTaskCount(taskCount += 1);
				assignmentTaskStatus.setUnReveiwed(underReviewed);
				assignmentTaskStatus.setReveiwed(reviewed);
				assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
				assignmentTaskStatus.setAssignmentTitle(assignment.getTitle());
				assignmentTaskStatusList.add(assignmentTaskStatus);
			}
		});
		return ResponseEntity.ok(assignmentTaskStatusList);
	}

	@Override
	public ResponseEntity<?> getOverAllAssignmentTaskStatus() {

		List<Assignment> assignments = assignmentRepository.findByIsDeletedFalse();
		assignments = AllAssignmentTemp(assignments);
		int totalSubmitted = 0;
		int underReviewed = 0;
		int reviewed = 0;
		SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
		for (Assignment assignment : assignments) {
			for (AssignmentTaskQuestion q : assignment.getAssignmentQuestion()) {
				List<AssignmentSubmission> submissionAssignments = submissionRepository
						.getSubmitAssignmentByAssignmentId(assignment.getId(), q.getQuestionId());
				totalSubmitted += submissionAssignments.size();

				for (AssignmentSubmission submission : submissionAssignments) {
					if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
						underReviewed += 1;
					} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
							|| submission.getStatus().equals(SubmissionStatus.Accepted)
							|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
						reviewed += 1;
					}
				}
			}
		}
		assignmentTaskStatus.setUnReveiwed(underReviewed);
		assignmentTaskStatus.setReveiwed(reviewed);
		assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
		return ResponseEntity.ok(assignmentTaskStatus);
	}

	@Override
	public ResponseEntity<?> getAllLockedAndUnlockedAssignment(Integer studentId) {

		Map<String, Object> response = new HashMap<>();
		List<Assignment> lockedAssignment = new ArrayList<>();
		List<Assignment> unLockedAssignment = new ArrayList<>();

		List<Assignment> allAssignment = assignmentRepository.findAllByCourseIdAndIsDeletedFalse(
				studentRepository.findById(studentId).get().getCourse().getCourseId());
		allAssignment = AllAssignmentTemp(allAssignment);
	 	System.err.println(allAssignment);
	 	
		if (!allAssignment.isEmpty()) {
			unLockedAssignment.add(allAssignment.get(0));
		}

		// unlocking the assignments
		int index = 0;
		if (!allAssignment.isEmpty()) {
			for (int i = 0; i < allAssignment.size(); i++) {

				List<AssignmentTaskQuestion> questions = allAssignment.get(i).getAssignmentQuestion();
				List<AssignmentSubmission> submittedAssignment = submissionRepository
						.findByAssignmentIdAndStudentId(allAssignment.get(i).getId(), studentId);

				int taskCount = 0;
				for (AssignmentSubmission submission : submittedAssignment) {
					if ("Accepted".equals(submission.getStatus().name())
							|| "Rejected".equals(submission.getStatus().name())
							|| "Reviewing".equals(submission.getStatus().name())) {
						taskCount++;
					}
				}

				if (taskCount == questions.size()) {
					if (i < allAssignment.size() - 1) {
						unLockedAssignment.add(allAssignment.get(index + 1));
					}
				} else {
					for (int j = index + 1; j < allAssignment.size(); j++) {
						lockedAssignment.add(allAssignment.get(i));
					}
					break;
				}
				index++;
			}
		}

		response.put("lockedAssignment", lockedAssignment);
		response.put("unLockedAssignment", unLockedAssignment);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAssignmentQuesSubmissionStatus(Long questionId, Long assignmentId, Integer studentId) {
		AssignmentSubmission submission = submissionRepository.findByAssignmentIdAndQuestionIdAndStudentId(assignmentId,
				questionId, studentId);
		if (Objects.nonNull(submission)) {
//			if ( "Rejected".equals(submission.getStatus().name()))
//				return new ResponseEntity<>(false, HttpStatus.OK);
//			else
				return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

	@Override
	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatusByCourseIdAndSubjectId(Integer courseId,
			Integer subjectId) {

		List<Assignment> assignments = new ArrayList<>();
		if (subjectId == 0) {
			assignments = assignmentRepository.findAllByCourseIdAndIsDeletedFalse(courseId);
		} else {
			assignments = assignmentRepository.findAllByCourseIdAndSubjectIdAndIsDeletedFalse(courseId, subjectId);
		}
		 assignments = AllAssignmentTemp(assignments);
		List<SubmissionAssignmentTaskStatus> assignmentTaskStatusList = new ArrayList<>();

		assignments.forEach(assignment -> {
			int taskCount = 0;
	
			List<AssignmentTaskQuestion> questions = assignment.getAssignmentQuestion();
			for (AssignmentTaskQuestion q : questions) {
				int totalSubmitted = 0;
				int underReviewed = 0;
				int reviewed = 0;

				SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
				assignmentTaskStatus.setAssignmentId(assignment.getId());

				List<AssignmentSubmission> submissionAssignments = submissionRepository
						.getSubmitAssignmentByAssignmentId(assignment.getId(), q.getQuestionId());
				totalSubmitted += submissionAssignments.size();
				for (AssignmentSubmission submission : submissionAssignments) {
					if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
						underReviewed += 1;
					} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
							|| submission.getStatus().equals(SubmissionStatus.Accepted)
							|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
						reviewed += 1;
					}
				}
				assignmentTaskStatus.setTaskCount(taskCount += 1);
				assignmentTaskStatus.setUnReveiwed(underReviewed);
				assignmentTaskStatus.setReveiwed(reviewed);
				assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
				assignmentTaskStatus.setAssignmentTitle(assignment.getTitle());
				assignmentTaskStatusList.add(assignmentTaskStatus);
			}
		});
		return ResponseEntity.ok(assignmentTaskStatusList);
	}

	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatusByCourseId(Integer courseId) {

		Optional<Course> findByCourseId = courseRepo.findByCourseId(courseId);
		if (Objects.nonNull(findByCourseId)) {
			List<Assignment> assignments = assignmentRepository.findAllByCourseIdAndIsDeletedFalse(courseId);
			assignments =AllAssignmentTemp(assignments);
			int totalSubmitted = 0;
			int underReviewed = 0;
			int reviewed = 0;
			SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
			for (Assignment assignment : assignments) {
				List<AssignmentTaskQuestion> questions = assignment.getAssignmentQuestion();
				for (AssignmentTaskQuestion q : questions) {
					List<AssignmentSubmission> submissionAssignments = submissionRepository
							.getSubmitAssignmentByAssignmentId(assignment.getId(), q.getQuestionId());
					totalSubmitted += submissionAssignments.size();

					for (AssignmentSubmission submission : submissionAssignments) {
						if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
							underReviewed += 1;
						} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
								|| submission.getStatus().equals(SubmissionStatus.Accepted)
								|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
							reviewed += 1;
						}
					}
				}
			}
			assignmentTaskStatus.setUnReveiwed(underReviewed);
			assignmentTaskStatus.setReveiwed(reviewed);
			assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
			return ResponseEntity.ok(assignmentTaskStatus);
		}
		return ResponseEntity.notFound().build();
	}

	@Override
	public ResponseEntity<?> deleteTaskQuestion(Long questionId){
		assignmentTaskQuestionRepository.deleteQuestionByIdAndId(questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}
    
	public  List<Assignment>AllAssignmentTemp(List<Assignment>list) {
		list = list.parallelStream().filter(obj->!obj.getIsDeleted()).collect(Collectors.toList());
		list.forEach(obj -> {
			 obj.setAssignmentQuestion(obj.getAssignmentQuestion().parallelStream().filter(obj1->!obj1.getIsDeleted()).collect(Collectors.toList()));
		});
		return list;
	}
	
	public  Assignment getAssignmentTemp(Assignment assignment) {
		 assignment.setAssignmentQuestion(assignment.getAssignmentQuestion().parallelStream().filter(obj->!obj.getIsDeleted()).collect(Collectors.toList()));
	     return assignment;
	}

}
