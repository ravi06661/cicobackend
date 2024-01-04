package com.cico.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedDeque;
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
import com.cico.payload.AssignmentFilterResponse;
import com.cico.payload.AssignmentRequest;
import com.cico.payload.AssignmentSubmissionRequest;
import com.cico.payload.AssignmentSubmissionResponse;
import com.cico.payload.AssignmentTaskFilterReponse;
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
//		Assignment assignment = assignmentRepository.findByIdAndIsDeleted(id, false)
//				.orElseThrow(() -> new ResourceNotFoundException("Assignment not found"));
//		assignment.setAssignmentQuestion(assignment.getAssignmentQuestion().parallelStream()
//				.filter(obj1 -> !obj1.getIsDeleted()).collect(Collectors.toList()));
//		return assignment;
		return null;
	}

	@Override
	public ResponseEntity<?> createAssignment(AssignmentRequest assignmentRequest) throws Exception {

		Optional<Assignment> obj = assignmentRepository.findByName(assignmentRequest.getTitle().trim());
		Map<String, Object> response = new HashMap<>();
		if (obj.isEmpty()) {
			Assignment assignment = new Assignment();
			assignment.setTitle(assignmentRequest.getTitle());

			assignment.setCourse(courseRepo.findById(assignmentRequest.getCourseId()).get());

			if (assignmentRequest.getSubjectId() != null)
				assignment.setSubject(subjectRepo.findById(assignmentRequest.getSubjectId()).get());

			assignment.setCreatedDate(LocalDateTime.now());
			assignment.setIsDeleted(false);
			Assignment savedAssignment = assignmentRepository.save(assignment);
			savedAssignment = getAssignmentTemp(savedAssignment);
			response.put(AppConstants.MESSAGE, AppConstants.SUCCESS);
			response.put("assignmentId", savedAssignment.getId());
			return new ResponseEntity<>(response, HttpStatus.CREATED);
		} else {
			throw new Exception("Assignmnet Already Present With This Title");
		}
	}

	@Override
	public ResponseEntity<?> getAllAssignments() {
		List<Assignment> assignments = assignmentRepository.findByIsDeletedFalse();
		assignments.forEach(obj -> {
			obj.setAssignmentQuestion(obj.getAssignmentQuestion().parallelStream().filter(obj1 -> !obj1.getIsDeleted())
					.collect(Collectors.toList()));
		});
		return new ResponseEntity<>("", HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAssignmentQuesById(Long questionId, Long assignmentId) { ///
		Map<String, Object> response = new HashMap<>();
		AssignmentTaskQuestion assignmentTaskQuestion = assignmentTaskQuestionRepository.findByQuestionId(questionId)
				.orElseThrow(() -> new ResourceNotFoundException(AppConstants.NO_DATA_FOUND));
		Assignment assignment = assignmentRepository.findByIdAndIsDeleted(assignmentId, false).get();
		AssignmentTaskFilterReponse obj = new AssignmentTaskFilterReponse();
		obj.setQuestion(assignmentTaskQuestion.getQuestion());
		obj.setQuestionId(assignmentTaskQuestion.getQuestionId());
		obj.setVideoUrl(assignmentTaskQuestion.getVideoUrl());
		obj.setQuestionImages(assignmentTaskQuestion.getQuestionImages());
		response.put("question", obj);
		response.put("attachment", assignment.getTaskAttachment());
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> submitAssignment(MultipartFile file, AssignmentSubmissionRequest readValue) ////
			throws Exception {

		Optional<AssignmentTaskQuestion> obj = assignmentTaskQuestionRepository.findByQuestionId(readValue.getTaskId());
		boolean anyMatch = obj.get().getAssignmentSubmissions().stream()
				.anyMatch(obj2 -> obj2.getStudent().getStudentId() == readValue.getStudentId());

		if (!anyMatch) {
			AssignmentSubmission submission = new AssignmentSubmission();
			submission.setStudent(studentRepository.findByStudentId(readValue.getStudentId()));
			submission.setDescription(readValue.getDescription());
			submission.setSubmissionDate(LocalDateTime.now());
			submission.setStatus(SubmissionStatus.Unreviewed);
			if (Objects.nonNull(file)) {
				String fileName = fileServiceImpl.uploadFileInFolder(file, QUESTION_IMAGES_DIR);
				submission.setSubmitFile(fileName);
			}
			AssignmentSubmission save = submissionRepository.save(submission);
			obj.get().getAssignmentSubmissions().add(save);
			assignmentTaskQuestionRepository.save(obj.get());
			return new ResponseEntity<>(HttpStatus.CREATED);
		} else {
			throw new Exception("ALREADY THIS ASSIGNMENT TASK SUBMITED!!");
		}
	}

	@Override
	public ResponseEntity<?> getSubmitedAssignmetByStudentId(Integer studentId) {
		List<Assignment> assignments = AllAssignmentTemp(assignmentRepository.findByIsDeletedFalse());
		ConcurrentLinkedDeque<AssignmentSubmissionResponse> resultList = new ConcurrentLinkedDeque<>();

		assignments.parallelStream()
				.flatMap(
						obj -> obj.getAssignmentQuestion().stream()
								.flatMap(obj2 -> obj2.getAssignmentSubmissions().parallelStream()
										.filter(obj3 -> obj3.getStudent() != null
												&& Objects.equals(obj3.getStudent().getStudentId(), studentId))
										.map(obj3 -> {
											AssignmentSubmissionResponse response = new AssignmentSubmissionResponse();
											response.setApplyForCourse(obj3.getStudent().getApplyForCourse());
											response.setFullName(obj3.getStudent().getFullName());
											response.setSubmissionDate(obj3.getSubmissionDate());
											response.setStatus(obj3.getStatus().toString());
											response.setProfilePic(obj3.getStudent().getProfilePic());
											response.setTitle(obj.getTitle());
											response.setAssginmentId(obj.getId());
											response.setTaskId(obj2.getQuestionId());
											response.setSubmitFile(obj3.getSubmitFile());
											response.setDescription(obj3.getDescription());
											return response;

										})))
				.forEach(resultList::add);
		return new ResponseEntity<>(resultList, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAllSubmitedAssginments() {
		List<Assignment> assignments = AllAssignmentTemp(assignmentRepository.findByIsDeletedFalse());
		ConcurrentLinkedDeque<AssignmentSubmissionResponse> resultList = new ConcurrentLinkedDeque<>();
		assignments.parallelStream().flatMap(obj -> obj.getAssignmentQuestion().stream()
				.flatMap(obj2 -> obj2.getAssignmentSubmissions().parallelStream().map(obj3 -> {

					AssignmentSubmissionResponse response = new AssignmentSubmissionResponse();
					response.setApplyForCourse(obj3.getStudent().getApplyForCourse());
					response.setFullName(obj3.getStudent().getFullName());
					response.setSubmissionDate(obj3.getSubmissionDate());
					response.setStatus(obj3.getStatus().toString());
					response.setProfilePic(obj3.getStudent().getProfilePic());
					response.setTitle(obj.getTitle());
					response.setAssginmentId(obj.getId());
					response.setTaskId(obj2.getQuestionId());
					response.setSubmitFile(obj3.getSubmitFile());
					response.setDescription(obj3.getDescription());
					response.setSubmissionId(obj3.getSubmissionId());
					return response;

				}))).forEach(resultList::add);
		return new ResponseEntity<>(resultList, HttpStatus.OK);
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
			// assignmentTaskQuestion.setAssignmentId(assignmentId);
			List<String> fileNames = questionImages.stream()
					.map(file -> fileServiceImpl.uploadFileInFolder(file, QUESTION_IMAGES_DIR))
					.collect(Collectors.toList());

			assignmentTaskQuestion.setQuestionImages(fileNames);

			assignment.getAssignmentQuestion().add(assignmentTaskQuestion);

			Assignment updatedAssignment = assignmentRepository.save(assignment);
			// updatedAssignment = getAssignmentTemp(updatedAssignment);
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

		List<Object[]> list = assignmentRepository.getAllSubmissionAssignmentTaskStatus();
		List<SubmissionAssignmentTaskStatus> assignmentTaskStatusList = new ArrayList<>();

		for (Object[] objects : list) {
			SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
			assignmentTaskStatus.setAssignmentId((long) objects[0]);
			assignmentTaskStatus.setAssignmentTitle((String) objects[1]);
			assignmentTaskStatus.setUnReveiwed(Objects.nonNull(objects[2]) ? (int) (long) objects[2] : 0);
			assignmentTaskStatus.setReveiwed((int) (long) objects[3]);
			assignmentTaskStatus.setTotalSubmitted((int) (long) objects[4]);
			assignmentTaskStatus.setTaskCount((int) (long) objects[5]);
			assignmentTaskStatus.setTaskId((Long) objects[6]);
			assignmentTaskStatusList.add(assignmentTaskStatus);
		}
		return ResponseEntity.ok(assignmentTaskStatusList);
//		List<Assignment> assignments = assignmentRepository.findByIsDeletedFalse();
//		assignments = AllAssignmentTemp(assignments);
//		List<SubmissionAssignmentTaskStatus> assignmentTaskStatusList = new ArrayList<>();
//		List<AssignmentSubmission> submissionAssignments = new ArrayList<>();
//		assignments.forEach(assignment -> {
//			submissionAssignments.clear();
//			int taskCount = 0;
//			for (AssignmentTaskQuestion q : assignment.getAssignmentQuestion()) {
//				int totalSubmitted = 0;
//				int underReviewed = 0;
//				int reviewed = 0;
//
//				SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
//				assignmentTaskStatus.setAssignmentId(assignment.getId());
//
//				List<AssignmentTaskQuestion> assignmentQuestion = assignment.getAssignmentQuestion();
//				assignmentQuestion.forEach(obj -> {
//					submissionAssignments.addAll(obj.getAssignmentSubmissions());
//				});
//
//				totalSubmitted += submissionAssignments.size();
//				for (AssignmentSubmission submission : submissionAssignments) {
//					if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
//						underReviewed += 1;
//					} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
//							|| submission.getStatus().equals(SubmissionStatus.Accepted)
//							|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
//						reviewed += 1;
//					}
//				}
//				assignmentTaskStatus.setTaskCount(taskCount += 1);
//				assignmentTaskStatus.setUnReveiwed(underReviewed);
//				assignmentTaskStatus.setReveiwed(reviewed);
//				assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
//				assignmentTaskStatus.setAssignmentTitle(assignment.getTitle());
//				assignmentTaskStatusList.add(assignmentTaskStatus);
//			}
//		});
		// return ResponseEntity.ok(assignmentTaskStatusList);
		// return ResponseEntity.ok("");
	}

	@Override
	public ResponseEntity<?> getOverAllAssignmentTaskStatus() {

		// List<Assignment> assignments = assignmentRepository.findByIsDeletedFalse();
		Object[] result = assignmentRepository.getOverAllAssignmentTaskStatus();
		SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
		Integer count = 0;
		// System.err.println( "--------------"+((Object[])result[0])[0].toString());
		for (Object obj : result) {
			if (obj instanceof Object[]) {
				// If the element is an array, iterate through the inner array and print its
				// elements
				Object[] innerArray = (Object[]) obj;
				for (Object innerObj : innerArray) {
					if (count == 0)
						assignmentTaskStatus.setUnReveiwed(Integer.parseInt(innerObj.toString()));
					if (count == 1)
						assignmentTaskStatus.setReveiwed(Integer.parseInt(innerObj.toString()));
					if (count == 2)
						assignmentTaskStatus.setTotalSubmitted(Integer.parseInt(innerObj.toString()));
					++count;
				}
				// ++count;
			} else {
				// If the element is not an array, print its value directly
				System.err.println("Value: 2222" + obj);
			}
		}
		// System.out.println((Long) list[1]);
		// System.out.println((Long)list[2]);

		// System.err.println(list[0]);
		// System.out.println((Long)list[2]);

//		assignments = AllAssignmentTemp(assignments);
//		int totalSubmitted = 0;
//		int underReviewed = 0;
//		int reviewed = 0;
//		List<AssignmentSubmission> submissionAssignments = new ArrayList<>();
		// SubmissionAssignmentTaskStatus assignmentTaskStatus = new
		// SubmissionAssignmentTaskStatus();
//		for (Assignment assignment : assignments) {
//			submissionAssignments.clear();
//
//			List<AssignmentTaskQuestion> assignmentQuestion = assignment.getAssignmentQuestion();
//			assignmentQuestion.forEach(obj -> {
//				submissionAssignments.addAll(obj.getAssignmentSubmissions());
//			});
//			totalSubmitted += submissionAssignments.size();
//			for (AssignmentSubmission submission : submissionAssignments) {
//				if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
//					underReviewed += 1;
//				} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
//						|| submission.getStatus().equals(SubmissionStatus.Accepted)
//						|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
//					reviewed += 1;
//				}
//			}
//		}

//		assignmentTaskStatus.setUnReveiwed((int) (long) list[0]);
//		assignmentTaskStatus.setReveiwed((int) (long) list[0]);
//		assignmentTaskStatus.setTotalSubmitted((int) (long) list[0]);
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

		if (!allAssignment.isEmpty()) {
			unLockedAssignment.add(allAssignment.get(0));
		}

		// unlocking the assignments
		int index = 0;
		List<AssignmentSubmission> submittedAssignment = new ArrayList<>();
		if (!allAssignment.isEmpty()) {
			for (int i = 0; i < allAssignment.size(); i++) {
				submittedAssignment.clear();

				List<AssignmentTaskQuestion> questions = allAssignment.get(i).getAssignmentQuestion();
				// getting total assignment question submitted
				questions.forEach(obj -> {
					submittedAssignment.addAll(obj.getAssignmentSubmissions().stream()
							.filter(obj2 -> obj2.getStudent().getStudentId() == studentId)
							.collect(Collectors.toList()));

				});

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

		// Customizing response
		List<AssignmentFilterResponse> assignmentFilterResponses = new ArrayList<>();
		unLockedAssignment.forEach(obj -> {
			AssignmentFilterResponse res = new AssignmentFilterResponse();
			List<AssignmentTaskFilterReponse> res1 = new ArrayList<>();
			res.setId(obj.getId());
			res.setTitle(obj.getTitle());
			obj.getAssignmentQuestion().forEach(obj2 -> {
				AssignmentTaskFilterReponse res2 = new AssignmentTaskFilterReponse();
				res2.setQuestionId(obj2.getQuestionId());
				res1.add(res2);
			});
			res.setTaskQuestion(res1);

			// getting total assignment question submitted
			obj.getAssignmentQuestion().forEach(obj2 -> {
				res.setTotalTaskCompleted(obj2.getAssignmentSubmissions().stream()
						.filter(obj3 -> obj3.getStudent().getStudentId() == studentId).collect(Collectors.toList())
						.size());
			});

			assignmentFilterResponses.add(res);
		});

		response.put("lockedAssignment", lockedAssignment.size());
		response.put("unLockedAssignment", assignmentFilterResponses);
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
			Integer subjectId) {

//		List<Assignment> assignments = new ArrayList<>();
//		if (subjectId == 0) {
//			assignments = assignmentRepository.findAllByCourseIdAndIsDeletedFalse(courseId);
//		} else {
//			assignments = assignmentRepository.findAllByCourseIdAndSubjectIdAndIsDeletedFalse(courseId, subjectId);
//		}
//		
//		assignments = AllAssignmentTemp(assignments);
//		
		// List<SubmissionAssignmentTaskStatus> assignmentTaskStatusList = new
		// ArrayList<>();
		// List<AssignmentSubmission> submittedAssignment = new ArrayList<>();

//		assignments.forEach(assignment -> {
//			int taskCount = 0;
//			submittedAssignment.clear();
//			List<AssignmentTaskQuestion> questions = assignment.getAssignmentQuestion();
//			for (AssignmentTaskQuestion q : questions) {
//				int totalSubmitted = 0;
//				int underReviewed = 0;
//				int reviewed = 0;
//
//				SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
//				assignmentTaskStatus.setAssignmentId(assignment.getId());
//				submittedAssignment.addAll(q.getAssignmentSubmissions());
//
//				totalSubmitted += submittedAssignment.size();
//				for (AssignmentSubmission submission : submittedAssignment) {
//					if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
//						underReviewed += 1;
//					} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
//							|| submission.getStatus().equals(SubmissionStatus.Accepted)
//							|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
//						reviewed += 1;
//					}
//				}
//				assignmentTaskStatus.setTaskCount(taskCount += 1);
//				assignmentTaskStatus.setUnReveiwed(underReviewed);
//				assignmentTaskStatus.setReveiwed(reviewed);
//				assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
//				assignmentTaskStatus.setAssignmentTitle(assignment.getTitle());
//				assignmentTaskStatusList.add(assignmentTaskStatus);
//			}
//		});

		List<Object[]> list = new ArrayList<>();
		if (subjectId == 0) {
			list = assignmentRepository.findAllAssignmentStatusWithCourseId(courseId);
		} else {
			list = assignmentRepository.findAllAssignmentStatusWithCourseIdAndSubjectId(courseId, subjectId);
		}

		List<SubmissionAssignmentTaskStatus> assignmentTaskStatusList = new ArrayList<>();
		// assignmentRepository.findByQuery(courseId);
		for (Object[] row : list) {
			SubmissionAssignmentTaskStatus res = new SubmissionAssignmentTaskStatus();
			res.setTaskId((long) row[0]);
			res.setTotalSubmitted((int) (long) row[1]);
			res.setUnReveiwed((int) (long) row[2]);
			res.setReveiwed((int) (long) row[3]);
			res.setTotalSubmitted((int) (long) row[4]);
			res.setTaskTitle((String) row[5]);
			res.setAssignmentTitle((String) row[5]);
			assignmentTaskStatusList.add(res);

		}
		return ResponseEntity.ok(assignmentTaskStatusList);
	}

	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatusByCourseId(Integer courseId) {

//		Optional<Course> findByCourseId = courseRepo.findByCourseId(courseId);
//		if (Objects.nonNull(findByCourseId)) {
//			List<Assignment> assignments = assignmentRepository.findAllByCourseIdAndIsDeletedFalse(courseId);
//			assignments = AllAssignmentTemp(assignments);
//			int totalSubmitted = 0;
//			int underReviewed = 0;
//			int reviewed = 0;
//			SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
//			for (Assignment assignment : assignments) {
//				List<AssignmentTaskQuestion> questions = assignment.getAssignmentQuestion();
//				for (AssignmentTaskQuestion q : questions) {
//					List<AssignmentSubmission> submissionAssignments = submissionRepository
//							.getSubmitAssignmentByAssignmentId(assignment.getId(), q.getQuestionId());
//					totalSubmitted += submissionAssignments.size();
//
//					for (AssignmentSubmission submission : submissionAssignments) {
//						if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
//							underReviewed += 1;
//						} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
//								|| submission.getStatus().equals(SubmissionStatus.Accepted)
//								|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
//							reviewed += 1;
//						}
//					}
//				}
//			}
//			assignmentTaskStatus.setUnReveiwed(underReviewed);
//			assignmentTaskStatus.setReveiwed(reviewed);
//			assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
//			return ResponseEntity.ok(assignmentTaskStatus);
//		}
//		return ResponseEntity.notFound().build();
		return ResponseEntity.ok(null);
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

	public Assignment getAssignmentTemp(Assignment assignment) {
		assignment.setAssignmentQuestion(assignment.getAssignmentQuestion().parallelStream()
				.filter(obj -> !obj.getIsDeleted()).collect(Collectors.toList()));
		return assignment;
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
			response.setTitle(sub.getTitle());
			response.setSubmitFile(sub.getSubmitFile());
			response.setDescription(sub.getDescription());
			response.setStatus(sub.getStatus().toString());

			return new ResponseEntity<>(response, HttpStatus.OK);
		} else {
			res.put("status", "no_found");
			res.put("data", null);
			return new ResponseEntity<>(res, HttpStatus.NOT_FOUND);
		}

	}

}
