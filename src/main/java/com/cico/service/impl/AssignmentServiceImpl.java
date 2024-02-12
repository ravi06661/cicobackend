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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cico.exception.ResourceAlreadyExistException;
import com.cico.exception.ResourceNotFoundException;
import com.cico.model.Assignment;
import com.cico.model.AssignmentSubmission;
import com.cico.model.AssignmentTaskQuestion;
import com.cico.payload.AssignmentAndTaskSubmission;
import com.cico.payload.AssignmentFilterResponse;
import com.cico.payload.AssignmentRequest;
import com.cico.payload.AssignmentResponse;
import com.cico.payload.AssignmentSubmissionRequest;
import com.cico.payload.AssignmentSubmissionResponse;
import com.cico.payload.AssignmentTaskFilterReponse;
import com.cico.payload.CourseResponse;
import com.cico.payload.SubjectResponse;
import com.cico.payload.TaskQuestionResponse;
import com.cico.payload.TaskStatusSummary;
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

//	@Value("${questionImages}")
//	private String QUESTION_IMAGES_DIR;
//	@Value("${attachmentFiles}")
//	private String ATTACHMENT_FILES_DIR;

	@Autowired
	private FileServiceImpl fileServiceImpl;

	@Autowired
	private AssignmentTaskQuestionRepository assignmentTaskQuestionRepository;

	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private AssignmentSubmissionRepository submissionRepository;

	public Assignment checkIsPresent(Long id) {
		return assignmentRepository.findByIdAndIsDeleted(id, false)
				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
	}

	@Override
	public ResponseEntity<?> getAssignment(Long id) {
		Assignment assignment = checkIsPresent(id);
		Map<String, Object> response = new HashMap<>();
		response.put(AppConstants.MESSAGE, AppConstants.DATA_FOUND);
		response.put("assignment", assignmentResponseFilter(assignment));

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> createAssignment(AssignmentRequest assignmentRequest) {

		Optional<Assignment> obj = assignmentRepository.findByName(assignmentRequest.getTitle().trim());
		Map<String, Object> response = new HashMap<>();
		if (obj.isEmpty()) {
			Assignment assignment = new Assignment();
			assignment.setTitle(assignmentRequest.getTitle().trim());

			assignment.setCourse(courseRepo.findById(assignmentRequest.getCourseId()).get());

			if (assignmentRequest.getSubjectId() != null)
				assignment.setSubject(subjectRepo.findById(assignmentRequest.getSubjectId()).get());

			assignment.setCreatedDate(LocalDateTime.now());
			assignment.setIsDeleted(false);
			Assignment savedAssignment = assignmentRepository.save(assignment);
			// savedAssignment = getAssignmentTemp(savedAssignment);
			response.put(AppConstants.MESSAGE, AppConstants.SUCCESS);
			response.put("assignmentId", savedAssignment.getId());
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			throw new ResourceAlreadyExistException("Assignmnet Already Present With This Title");
		}
	}

	@Override
	public ResponseEntity<?> getAllAssignments() {
		List<Assignment> assignments = assignmentRepository.findByIsDeletedFalse();
		Map<String, Object> response = new HashMap<>();
		response.put(AppConstants.MESSAGE, AppConstants.DATA_FOUND);

		List<AssignmentResponse> collect = assignments.parallelStream().filter(obj -> !obj.getIsDeleted())
				.map(this::assignmentResponseFilter).collect(Collectors.toList());
		return new ResponseEntity<>(collect, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAssignmentQuesById(Long questionId) { ///
		Map<String, Object> response = new HashMap<>();
		AssignmentTaskQuestion assignmentTaskQuestion = assignmentTaskQuestionRepository.findByQuestionId(questionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.NO_DATA_FOUND));

		AssignmentTaskFilterReponse obj = new AssignmentTaskFilterReponse();
		obj.setQuestion(assignmentTaskQuestion.getQuestion());
		obj.setQuestionId(assignmentTaskQuestion.getQuestionId());
		obj.setVideoUrl(assignmentTaskQuestion.getVideoUrl());
		obj.setQuestionImages(assignmentTaskQuestion.getQuestionImages());
		response.put("question", obj);

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> submitAssignment(MultipartFile file, AssignmentSubmissionRequest readValue) ////
	{

		Optional<AssignmentTaskQuestion> obj = assignmentTaskQuestionRepository.findByQuestionId(readValue.getTaskId());
		boolean anyMatch = obj.get().getAssignmentSubmissions().parallelStream()
				.anyMatch(obj2 -> obj2.getStudent().getStudentId() == readValue.getStudentId());

		if (!anyMatch) {
			AssignmentSubmission submission = new AssignmentSubmission();
			submission.setStudent(studentRepository.findByStudentId(readValue.getStudentId()));
			submission.setDescription(readValue.getDescription());
			submission.setSubmissionDate(LocalDateTime.now());
			submission.setStatus(SubmissionStatus.Unreviewed);
			if (Objects.nonNull(file)) {
				String fileName = fileServiceImpl.uploadFileInFolder(file, AppConstants.TASK_AND_ASSIGNMENT_SUBMISSION);
				submission.setSubmitFile(fileName);
			}
			AssignmentSubmission save = submissionRepository.save(submission);
			obj.get().getAssignmentSubmissions().add(save);
			assignmentTaskQuestionRepository.save(obj.get());
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			throw new ResourceAlreadyExistException("ALREADY THIS ASSIGNMENT TASK SUBMITED!!");
		}
	}

	@Override
	public ResponseEntity<?> getSubmitedAssignmetByStudentId(Integer studentId) {
		List<Object[]> res = submissionRepository.getSubmitAssignmentByStudentId(studentId);
		List<AssignmentSubmissionResponse> list = new ArrayList<>();
		res.forEach(obj -> {
			AssignmentSubmissionResponse submission = new AssignmentSubmissionResponse();
			submission.setReview((String) obj[0]);
			submission.setStatus(((SubmissionStatus) obj[1]).toString());
			submission.setSubmissionDate((LocalDateTime) obj[2]);
			submission.setSubmitFile((String) obj[3]);
			submission.setDescription((String) obj[4]);
			submission.setTitle((String) obj[5]);
			submission.setSubmissionId((Long) obj[6]);

			list.add(submission);
		});
		return new ResponseEntity<>(list, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> getAllSubmitedAssginments(Integer courseId, Integer subjectId, SubmissionStatus status) {

		return new ResponseEntity<>(
				assignmentRepository.findAllAssignmentSubmissionWithCourseIdAndSubjectId(courseId, subjectId, status),
				HttpStatus.OK);
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

		AssignmentSubmission sub = submissionRepository.findById(submissionId).get();

		AssignmentSubmissionResponse response = new AssignmentSubmissionResponse();
		response.setApplyForCourse(sub.getStudent().getApplyForCourse());
		response.setFullName(sub.getStudent().getFullName());
		response.setSubmissionDate(sub.getSubmissionDate());
		response.setStatus(sub.getStatus().toString());
		response.setProfilePic(sub.getStudent().getProfilePic());
		// response.setTitle(sub.getTitle());
		response.setSubmitFile(sub.getSubmitFile());
		response.setDescription(sub.getDescription());
		response.setStatus(sub.getStatus().toString());
		response.setReview(sub.getReview());
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> addQuestionInAssignment(String question, String videoUrl,
			List<MultipartFile> questionImages, Long assignmentId) {

		Assignment assignment = checkIsPresent(assignmentId);
		AssignmentTaskQuestion assignmentTaskQuestion = new AssignmentTaskQuestion();
		assignmentTaskQuestion.setQuestion(question);
		assignmentTaskQuestion.setVideoUrl(videoUrl);
		assignmentTaskQuestion.setIsDeleted(false);
		if (Objects.nonNull(questionImages)) {
			List<String> fileNames = questionImages.stream()
					.map(file -> fileServiceImpl.uploadFileInFolder(file, AppConstants.TASK_ASSIGNMENT_FILES))
					.collect(Collectors.toList());
			assignmentTaskQuestion.setQuestionImages(fileNames);
		}
		AssignmentTaskQuestion newQuestion = assignmentTaskQuestionRepository.save(assignmentTaskQuestion);
		assignment.getAssignmentQuestion().add(newQuestion);

		assignmentRepository.save(assignment);
		return new ResponseEntity<>(taskquestionResponseFilter(newQuestion), HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> addAssignment(Long assignmentId, MultipartFile attachment) {

		Assignment assignment = checkIsPresent(assignmentId);
		if (Objects.nonNull(assignment) && Objects.nonNull(attachment)) {
			String fileName = fileServiceImpl.uploadFileInFolder(attachment, AppConstants.TASK_ASSIGNMENT_FILES);
			assignment.setTaskAttachment(fileName);
		}

		assignmentRepository.save(assignment);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatus() {

		List<Object[]> list = assignmentRepository.getAllSubmissionAssignmentTaskStatus();
		List<AssignmentAndTaskSubmission> assignmentTaskStatusList = new ArrayList<>();

		for (Object[] objects : list) {
			AssignmentAndTaskSubmission assignmentTaskStatus = new AssignmentAndTaskSubmission();
			assignmentTaskStatus.setAssignmentId((long) objects[0]);
			assignmentTaskStatus.setAssignmentTitle((String) objects[1]);
			assignmentTaskStatus.setUnReveiwed(Objects.nonNull(objects[2]) ? (long) objects[2] : 0);
			assignmentTaskStatus.setReveiwed((long) objects[3]);
			assignmentTaskStatus.setTotalSubmitted((long) objects[4]);
			assignmentTaskStatus.setTaskCount((long) objects[5]);
			assignmentTaskStatus.setTaskId((Long) objects[6]);

			assignmentTaskStatusList.add(assignmentTaskStatus);
		}
		return ResponseEntity.ok(assignmentTaskStatusList);

	}

	@Override
	public ResponseEntity<?> getOverAllAssignmentTaskStatus() {
		TaskStatusSummary overAllAssignmentTaskStatus = assignmentRepository.getOverAllAssignmentTaskStatus();
		return ResponseEntity.ok(overAllAssignmentTaskStatus);
	}

//	@Override
//	public ResponseEntity<?> getAllLockedAndUnlockedAssignment(Integer studentId) {
//
//		Map<String, Object> response = new HashMap<>();
//		List<Assignment> lockedAssignment = new ArrayList<>();
//		List<Assignment> unLockedAssignment = new ArrayList<>();
//
//		List<Assignment> allAssignment = assignmentRepository.findAllByCourseIdAndIsDeletedFalse(
//				studentRepository.findById(studentId).get().getCourse().getCourseId());
//
//		if (allAssignment.isEmpty()) {
//			response.put(AppConstants.MESSAGE, AppConstants.ASSIGNMENT_NOT_FOUND);
//			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
//		}
//
//		allAssignment = AllAssignmentTemp(allAssignment);
//
//		if (!allAssignment.isEmpty()) {
//			unLockedAssignment.add(allAssignment.get(0));
//		}
//
//		// unlocking the assignments
////		int index = 0;
////		List<AssignmentSubmission> submittedAssignment = new ArrayList<>();
////		if (!allAssignment.isEmpty()) {
////			for (int i = 0; i < allAssignment.size(); i++) {
////				submittedAssignment.clear();
////
////				List<AssignmentTaskQuestion> questions = allAssignment.get(i).getAssignmentQuestion();
////				// getting total assignment question submitted
////				questions.forEach(obj -> {
////					submittedAssignment.addAll(obj.getAssignmentSubmissions().stream()
////							.filter(obj2 -> obj2.getStudent().getStudentId() == studentId)
////							.collect(Collectors.toList()));
////					System.err.println(submittedAssignment);
////
////				});
////
////				int taskCount = 0;
////				for (AssignmentSubmission submission : submittedAssignment) {
////					if ("Accepted".equals(submission.getStatus().name())
////							|| "Rejected".equals(submission.getStatus().name())
////							|| "Reviewing".equals(submission.getStatus().name())) {
////						taskCount++;
////					}
////				}
////				System.out.println(taskCount);
////				if (taskCount == questions.size()) {
////					if (i < allAssignment.size() - 1) {
////						unLockedAssignment.add(allAssignment.get(index + 1));
////					}
////				} else {
////					for (int j = index + 1; j < allAssignment.size(); j++) {
////						lockedAssignment.add(allAssignment.get(i));
////					}
////					break;
////				}
////				index++;
////			}
////		}
//
//		// Customizing response
////		List<AssignmentFilterResponse> assignmentFilterResponses = new ArrayList<>();
////		unLockedAssignment.forEach(obj -> {
////			AssignmentFilterResponse res = new AssignmentFilterResponse();
////			List<AssignmentTaskFilterReponse> res1 = new ArrayList<>();
////			res.setId(obj.getId());
////			res.setTitle(obj.getTitle());
////
////			obj.getAssignmentQuestion().forEach(obj2 -> {
////				AssignmentTaskFilterReponse res2 = new AssignmentTaskFilterReponse();
////				res2.setQuestionId(obj2.getQuestionId());
////				res1.add(res2);
////			});
////			res.setTaskQuestion(res1);
////
////			// getting total assignment question submitted
////			obj.getAssignmentQuestion().forEach(obj2 -> {
////				if (obj2.getAssignmentSubmissions().stream()
////						.anyMatch(obj3 -> obj3.getStudent().getStudentId() == studentId)) {
////					size++;
////				}
////			});
////			res.setTotalTaskCompleted(size);
////			assignmentFilterResponses.add(res);
//
////		});
//
//		for (int i = 0; i < allAssignment.size(); i++) {
//			Assignment assignment = allAssignment.get(i);
//			List<AssignmentSubmission> submittedAssignment = assignment.getAssignmentQuestion().stream()
//					.flatMap(question -> question.getAssignmentSubmissions().stream()
//							.filter(submission -> submission.getStudent().getStudentId() == studentId))
//					.collect(Collectors.toList());
//
//			long taskCount = submittedAssignment.stream().filter(
//					submission -> List.of("Accepted", "Rejected", "Reviewing").contains(submission.getStatus().name()))
//					.count();
//
//			if (taskCount == assignment.getAssignmentQuestion().size()) {
//				if (i < allAssignment.size() - 1) {
//					unLockedAssignment.add(allAssignment.get(i + 1));
//				}
//			} else {
//				for (int j = i + 1; j < allAssignment.size(); j++) {
//					lockedAssignment.add(allAssignment.get(j));
//				}
//				// lockedAssignment.add(assignment);
//				break;
//			}
//		}
//		List<AssignmentFilterResponse> assignmentFilterResponses = unLockedAssignment.stream().map(obj -> {
//			AssignmentFilterResponse res = new AssignmentFilterResponse();
//			res.setId(obj.getId());
//			res.setTitle(obj.getTitle());
//			res.setTaskQuestion(obj.getAssignmentQuestion().stream()
//					.map(obj2 -> new AssignmentTaskFilterReponse(obj2,studentId)).collect(Collectors.toList()));
//			res.setTotalTaskCompleted(
//					(int) obj.getAssignmentQuestion().stream().flatMap(obj2 -> obj2.getAssignmentSubmissions().stream())
//							.filter(obj3 -> obj3.getStudent().getStudentId() == studentId).count());
//			return res;
//		}).collect(Collectors.toList());
//
//		response.put("lockedAssignment", lockedAssignment.size());
//		response.put("unLockedAssignment", assignmentFilterResponses);
//
//		return new ResponseEntity<>(response, HttpStatus.OK);
//	}

	@Override
	public ResponseEntity<?> getAllLockedAndUnlockedAssignment(Integer studentId) {

		Map<String, Object> response = new HashMap<>();
		List<Assignment> lockedAssignment = new ArrayList<>();
		List<Assignment> unLockedAssignment = new ArrayList<>();

		List<Assignment> allAssignment = assignmentRepository.findAllByCourseIdAndIsDeletedFalse(
				studentRepository.findById(studentId).get().getCourse().getCourseId());

		if (allAssignment.isEmpty()) {
			response.put(AppConstants.MESSAGE, AppConstants.ASSIGNMENT_NOT_FOUND);
			return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
		}

		// allAssignment = AllAssignmentTemp(allAssignment);

		if (!allAssignment.isEmpty()) {
			unLockedAssignment.add(allAssignment.get(0));
		}

		for (int i = 0; i < allAssignment.size(); i++) {
			Assignment assignment = allAssignment.get(i);
			List<AssignmentSubmission> submittedAssignment = assignment.getAssignmentQuestion().stream()
					.flatMap(question -> question.getAssignmentSubmissions().stream()
							.filter(submission -> submission.getStudent().getStudentId() == studentId))
					.collect(Collectors.toList());

			long taskCount = submittedAssignment.stream().filter(
					submission -> List.of("Accepted", "Rejected", "Reviewing").contains(submission.getStatus().name()))
					.count();

			if (taskCount == assignment.getAssignmentQuestion().size()) {
				if (i < allAssignment.size() - 1) {
					unLockedAssignment.add(allAssignment.get(i + 1));
				}
			} else {
				for (int j = i + 1; j < allAssignment.size(); j++) {
					lockedAssignment.add(allAssignment.get(j));
				}
				// lockedAssignment.add(assignment);
				break;
			}
		}

		int count = 0;
		if (unLockedAssignment.size() == 1 && lockedAssignment.size() > 1) {

			List<AssignmentSubmission> collect = unLockedAssignment.get(0).getAssignmentQuestion().stream()
					.flatMap(q -> q.getAssignmentSubmissions().stream()
							.filter(q2 -> q2.getStudent().getStudentId() == studentId))
					.collect(Collectors.toList());
			if (collect.size() == unLockedAssignment.get(0).getAssignmentQuestion().size()) {
				unLockedAssignment.add(allAssignment.remove(1));
			}

		} else if (allAssignment.size() > 2) {
			int startIndex = Math.max(0, unLockedAssignment.size() - 2);
			for (int i = startIndex; i < unLockedAssignment.size(); i++) {
				List<AssignmentSubmission> collect = unLockedAssignment.get(i).getAssignmentQuestion().stream()
						.flatMap(q -> q.getAssignmentSubmissions().stream()
								.filter(q2 -> q2.getStudent().getStudentId() == studentId))
						.collect(Collectors.toList());
				boolean anyMatch = collect.stream()
						.anyMatch(submission1 -> "Unreviewed".equals(submission1.getStatus().name()));
				if (collect.size() == unLockedAssignment.get(i).getAssignmentQuestion().size() && anyMatch) {
					count += 1;
				}
			}
			if (count == 1) {
				if (lockedAssignment.size() > 0) {
					unLockedAssignment.add(lockedAssignment.remove(0));
				}
			}
			count = 0;

		}

		List<AssignmentFilterResponse> assignmentFilterResponses = unLockedAssignment.stream().map(obj -> {
			AssignmentFilterResponse res = new AssignmentFilterResponse();
			res.setId(obj.getId());
			res.setTitle(obj.getTitle());
			res.setStatus(obj.getIsActive());
			res.setTaskQuestion(obj.getAssignmentQuestion().stream()
					.map(obj2 -> new AssignmentTaskFilterReponse(obj2, studentId)).collect(Collectors.toList()));
			res.setTotalTaskCompleted(
					(int) obj.getAssignmentQuestion().parallelStream().flatMap(obj2 -> obj2.getAssignmentSubmissions().stream())
							.filter(obj3 -> obj3.getStudent().getStudentId() == studentId).count());
			return res;
		}).collect(Collectors.toList());
		response.put("unLockedAssignment", assignmentFilterResponses);
		response.put("lockedAssignment", lockedAssignment.size());

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAssignmentQuesSubmissionStatus(Long questionId, Integer studentId) {

		boolean status = assignmentTaskQuestionRepository.checkTaskSubmissionExistence(questionId, studentId);

		Map<String, Object> response = new HashMap<>();

		response.put(AppConstants.STATUS, status);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	@Override
	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatusByCourseIdAndSubjectId(Integer courseId,
			Integer subjectId, Integer pageNumber, Integer pageSize) {
		Page<AssignmentAndTaskSubmission> res = assignmentRepository.findAllAssignmentStatusWithCourseIdAndSubjectId(
				courseId, subjectId, PageRequest.of(pageNumber, pageSize));
		return ResponseEntity.ok(res);

	}

	@Override
	public ResponseEntity<?> deleteTaskQuestion(Long questionId) {
		assignmentTaskQuestionRepository.deleteQuestionByIdAndId(questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	public List<Assignment> AllAssignmentTemp(List<Assignment> list) {
		list = list.parallelStream().filter(obj -> !obj.getIsDeleted()).collect(Collectors.toList());
		list.forEach(obj -> {
			obj.setAssignmentQuestion(obj.getAssignmentQuestion().parallelStream().filter(obj1 -> !obj1.getIsDeleted())
					.collect(Collectors.toList()));
		});
		return list;
	}

	public AssignmentSubmissionResponse assignmentSubmissionResponse(AssignmentSubmission response) {

		AssignmentSubmissionResponse response2 = new AssignmentSubmissionResponse();

		response2.setApplyForCourse(response.getStudent().getApplyForCourse());
		response2.setFullName(response.getStudent().getFullName());
		response2.setSubmissionDate(response.getSubmissionDate());
		response2.setStatus(response.getStatus().toString());
		response2.setProfilePic(response.getStudent().getProfilePic());
		// response2.setTitle(response.getTitle());
		// response2.setAssignmentTitle("");
		return response2;
	}

	@Override
	public ResponseEntity<?> getSubmittedAssignmentBySubmissionId(Long submissionId) {

		Map<String, Object> res = new HashMap<>();
		Optional<AssignmentSubmission> submission = submissionRepository.findBySubmissionId(submissionId);
		if (submission != null) {
			AssignmentSubmission sub = submission.get();
			AssignmentSubmissionResponse response = new AssignmentSubmissionResponse();
			response.setApplyForCourse(sub.getStudent().getApplyForCourse());
			response.setFullName(sub.getStudent().getFullName());
			response.setSubmissionDate(sub.getSubmissionDate());
			response.setStatus(sub.getStatus().toString());
			response.setProfilePic(sub.getStudent().getProfilePic());
			// response.setTitle(sub.getTitle());
			response.setSubmitFile(sub.getSubmitFile());
			response.setDescription(sub.getDescription());
			response.setStatus(sub.getStatus().toString());
			response.setReview(sub.getReview());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			res.put(AppConstants.STATUS, AppConstants.NO_DATA_FOUND);
			return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
		}

	}

	@Override
	public ResponseEntity<?> updateAssignmentQuestion(Long questionId, String question, String videoUrl,
			List<String> questionImages, List<MultipartFile> newImages) {
		Map<String, Object> response = new HashMap<>();
		AssignmentTaskQuestion assignmentTaskQuestion = assignmentTaskQuestionRepository.findByQuestionId(questionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.NO_DATA_FOUND));

		assignmentTaskQuestion.setQuestion(question);
		assignmentTaskQuestion.setVideoUrl(videoUrl);
		if (Objects.isNull(questionImages)) {
			assignmentTaskQuestion.setQuestionImages(new ArrayList<String>());
		} else {
			assignmentTaskQuestion.setQuestionImages(questionImages);
		}
		if (Objects.nonNull(newImages) && newImages.size() > 0) {

			List<String> fileNames = newImages.stream()
					.map(file -> fileServiceImpl.uploadFileInFolder(file, AppConstants.TASK_ASSIGNMENT_FILES))
					.collect(Collectors.toList());
			assignmentTaskQuestion.getQuestionImages().addAll(fileNames);
		}
		AssignmentTaskQuestion save = assignmentTaskQuestionRepository.save(assignmentTaskQuestion);
		response.put(AppConstants.MESSAGE, AppConstants.UPDATE_SUCCESSFULLY);
		response.put("question", save);
		return new ResponseEntity<>(response, HttpStatus.OK);

	}

	public AssignmentResponse assignmentResponseFilter(Assignment a) {

		AssignmentResponse res = new AssignmentResponse();
		CourseResponse cr = new CourseResponse();
		SubjectResponse sr = new SubjectResponse();

		res.setId(a.getId());
		res.setTaskAttachment(a.getTaskAttachment());
		res.setTitle(a.getTitle());
		res.setCreatedDate(a.getCreatedDate());

		cr.setCourseName(a.getCourse().getCourseName());
		cr.setCourseId(a.getCourse().getCourseId());

		sr.setSubjectId(a.getSubject().getSubjectId());
		sr.setSubjectName(a.getSubject().getSubjectName());

		res.setAssignmentQuestion(a.getAssignmentQuestion().parallelStream().filter(obj -> !obj.getIsDeleted())
				.map(this::taskquestionResponseFilter).collect(Collectors.toList()));
		res.setCourse(cr);
		res.setSubject(sr);

		return res;

	}

	public TaskQuestionResponse taskquestionResponseFilter(AssignmentTaskQuestion obj) {
		return new TaskQuestionResponse(obj.getQuestionId(), obj.getQuestion(), obj.getQuestionImages(),
				obj.getVideoUrl());
	}

	@Override
	public ResponseEntity<?> activateAssignment(Long id) {
		Assignment assigment = checkIsPresent(id);
		if (assigment.getIsActive())
			assigment.setIsActive(false);
		else
			assigment.setIsActive(true);
		Map<String, Object> res = new HashMap<>();
		res.put(AppConstants.STATUS, assignmentRepository.save(assigment).getIsActive());
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

}
