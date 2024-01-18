package com.cico.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cico.exception.ResourceAlreadyExistException;
import com.cico.exception.ResourceNotFoundException;
import com.cico.model.AssignmentSubmission;
import com.cico.model.Course;
import com.cico.model.Student;
import com.cico.model.Subject;
import com.cico.model.Task;
import com.cico.model.TaskQuestion;
import com.cico.model.TaskSubmission;
import com.cico.payload.SubmissionAssignmentTaskStatus;
import com.cico.payload.TaskFilterRequest;
import com.cico.payload.TaskRequest;
import com.cico.payload.TaskSubmissionResponse;
import com.cico.repository.CourseRepository;
import com.cico.repository.StudentRepository;
import com.cico.repository.SubjectRepository;
import com.cico.repository.TaskQuestionRepository;
import com.cico.repository.TaskRepo;
import com.cico.repository.TaskSubmissionRepository;
import com.cico.service.ITaskService;
import com.cico.util.SubmissionStatus;

@Service
public class TaskServiceImpl implements ITaskService {

	@Autowired
	TaskRepo taskRepo;

	@Autowired
	FileServiceImpl fileService;

	@Value("${questionImages}")
	private String QUESTION_IMAGES_DIR;

	@Value("${attachmentFiles}")
	private String ATTACHMENT_FILES_DIR;

	@Autowired
	CourseServiceImpl courseService;

	@Autowired
	SubjectServiceImpl subjectService;

	@Autowired
	SubjectRepository subjectRepo;

	@Autowired
	private TaskQuestionRepository taskQuestionRepository;
	@Autowired
	private StudentRepository studentRepository;

	@Autowired
	private TaskSubmissionRepository taskSubmissionRepository;
	@Autowired
	private CourseRepository courseRepository;

	@Override
	public Task createTask(TaskRequest taskRequest) {
		if (taskRepo.findByTaskName(taskRequest.getTaskName()) != null)
			throw new ResourceAlreadyExistException("Task already exist");

		Task task = new Task();
		task.setAttachmentStatus(taskRequest.getAttachmentStatus());
		task.setCourse(taskRequest.getCourse());
		task.setSubject(taskRequest.getSubject());
		task.setTaskName(taskRequest.getTaskName());
		task.setIsDeleted(false);
		return taskRepo.save(task);
	}

	@Override
	public void updateTaskStatus(Long taskId) {
//		Task task = taskRepo.findById(taskId).orElseThrow(() -> new ResourceNotFoundException("Task not found"));
//
//		if (task.getIsActive().equals(true))
//			task.setIsActive(false);
//		else
//			task.setIsActive(true);
//
//		taskRepo.save(task);

	}

	@Override
	public List<Task> getFilteredTasks(TaskFilterRequest taskFilter) {
		Example<Task> example = null;

		Course course = courseRepository.findByCourseIdAndIsDeleted(taskFilter.getCourseId(),false);
		Subject subject = subjectRepo.findById(taskFilter.getSubjectId()).get();

		Task task = new Task();
		task.setCourse(course);
		task.setSubject(subject);
		task.setIsDeleted(taskFilter.getStatus());
		example = Example.of(task);
		return filterTasks(taskRepo.findAll(example));

	}

	@Override
	public Task getTaskById(Long taskId) {
		Task task = taskRepo.findById(taskId)
				.orElseThrow(() -> new ResourceNotFoundException("TASK NOT FOUND WITH THIS ID"));
		return filterTask(task);

	}

	@Override
	public List<Task> getAllTask() {
		List<Task> list = taskRepo.findAll();
		if (list.isEmpty())
			throw new ResourceNotFoundException("Task not Found");
		else
			return filterTasks(list);
	}

	@Override
	public ResponseEntity<?> studentTaskSubmittion(Long taskId, Integer studentId, MultipartFile file,
			String taskDescription) throws Exception {
		AssignmentSubmission obj = taskSubmissionRepository.findByTaskIdAndStudentId(taskId, studentId);
//		if (Objects.nonNull(obj) && obj.getStatus().name().equals("Rejected") || !Objects.nonNull(obj)) {
		if (Objects.nonNull(obj)) {
			TaskSubmission submittion = new TaskSubmission();
			submittion.setStudent(studentRepository.findByStudentId(studentId));
			if (Objects.nonNull(file)) {
				String f = fileService.uploadFileInFolder(file, ATTACHMENT_FILES_DIR);
				submittion.setSubmittionFileName(f);
			}
			submittion.setTaskId(taskId);
			submittion.setStatus(SubmissionStatus.Unreviewed);
			submittion.setSubmissionDate(LocalDateTime.now());
			submittion.setTaskDescription(taskDescription);
			TaskSubmission object = taskSubmissionRepository.save(submittion);
			if (!Objects.isNull(object)) {
				return new ResponseEntity<>(HttpStatus.OK);
			}
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		} else {
			throw new Exception("ALREADY TASK SUBMITED");
		}
	}

	@Override
	public ResponseEntity<?> addQuestionInTask(String question, String videoUrl, List<MultipartFile> questionImages,
			Long taskId) {
		Optional<Task> taskOptional = taskRepo.findByTaskIdAndIsDeleted(taskId, false);

		if (taskOptional.isPresent()) {
			TaskQuestion taskQuestion = new TaskQuestion();
			taskQuestion.setQuestion(question);
			taskQuestion.setVideoUrl(videoUrl);
			List<String> list = new ArrayList<>();

			questionImages.forEach((t) -> {
				String fileName = fileService.uploadFileInFolder(t, QUESTION_IMAGES_DIR);
				list.add(fileName);
			});
			taskQuestion.setQuestionImages(list);
			taskQuestion.setIsDeleted(false);
			Task task = taskOptional.get();
			task.getTaskQuestion().add(taskQuestion);

			Task save = taskRepo.save(task);
			save = filterTask(save);
			return new ResponseEntity<>(save, HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> addTaskAttachment(Long taskId, MultipartFile attachment) {
		Optional<Task> task = taskRepo.findByTaskIdAndIsDeleted(taskId, false);

		if (task.isPresent()) {
			String fileName = fileService.uploadFileInFolder(attachment, ATTACHMENT_FILES_DIR);
			task.get().setTaskAttachment(fileName);
			taskRepo.save(task.get());
			return new ResponseEntity<>(HttpStatus.OK);
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}

	@Override
	public ResponseEntity<?> deleteTaskQuestion(Long questionId) {
		taskQuestionRepository.deleteTaskQuestion(questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> getAllSubmitedTasks() {
		
		return new ResponseEntity<>(
				taskSubmissionRepository.findAll().stream().map(obj -> taskResponse(obj)).collect(Collectors.toList()),
				HttpStatus.OK);
	}

	public ResponseEntity<?> getAllSubmissionTaskStatus() {

		List<Task> tasks = taskRepo.findByIsDeletedFalse();
		tasks = filterTasks(tasks);
		List<SubmissionAssignmentTaskStatus> assignmentTaskStatusList = new ArrayList<>();

		tasks.forEach(task -> {
			SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
			int totalSubmitted = 0;
			int underReviewed = 0;
			int reviewed = 0;
			int taskCount = 0;
			taskCount += 1;
			List<TaskSubmission> taskSubmission = taskSubmissionRepository.getSubmittedTaskByTaskId(task.getTaskId());

			totalSubmitted += taskSubmission.size();
			for (TaskSubmission submission : taskSubmission) {
				if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
					underReviewed += 1;
				} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
						|| submission.getStatus().equals(SubmissionStatus.Accepted)
						|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
					reviewed += 1;
				}
			}
			assignmentTaskStatus.setTaskId(task.getTaskId());
			assignmentTaskStatus.setTaskCount(taskCount);
			assignmentTaskStatus.setUnReveiwed(underReviewed);
			assignmentTaskStatus.setReveiwed(reviewed);
			assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
			assignmentTaskStatus.setTaskTitle(task.getTaskName());
			assignmentTaskStatusList.add(assignmentTaskStatus);
		});
		return ResponseEntity.ok(assignmentTaskStatusList);
	}

	public ResponseEntity<?> getSubmitedTaskForStudent(Integer studentId) {

		List<Object[]> list1 = taskSubmissionRepository.getSubmitedTaskForStudent(studentId);
		List<TaskSubmission> list = new ArrayList<>();
		list1.forEach(obj -> {
			TaskSubmission submission = new TaskSubmission();
			submission.setReview((String) obj[0]);
			submission.setStatus((SubmissionStatus) obj[1]);
			submission.setSubmissionDate((LocalDateTime) obj[2]);
			submission.setSubmittionFileName((String) obj[3]);
			submission.setTaskDescription((String) obj[4]);
			submission.setTaskId((Long) obj[5]);
			submission.setTaskName((String) obj[6]);
			list.add(submission);
		});

		return new ResponseEntity<>(list, HttpStatus.OK);
	}

	@Override
	public ResponseEntity<?> updateSubmitedTaskStatus(Integer submissionId, String status, String review) {
		if (status.equals(SubmissionStatus.Reviewing.toString())) {
			taskSubmissionRepository.updateSubmitTaskStatus(submissionId, SubmissionStatus.Reviewing, review);
		} else if (status.equals(SubmissionStatus.Accepted.toString())) {
			taskSubmissionRepository.updateSubmitTaskStatus(submissionId, SubmissionStatus.Accepted, review);
		} else if (status.equals(SubmissionStatus.Rejected.toString())) {
			taskSubmissionRepository.updateSubmitTaskStatus(submissionId, SubmissionStatus.Rejected, review);
		}
		return new ResponseEntity<>(taskSubmissionRepository.findBySubmissionId(submissionId), HttpStatus.CREATED);
	}

	@Override
	public ResponseEntity<?> getOverAllTaskStatusforBarChart() {
		List<Task> tasks = taskRepo.findByIsDeletedFalse();
		tasks = filterTasks(tasks);
		SubmissionAssignmentTaskStatus assignmentTaskStatus = new SubmissionAssignmentTaskStatus();
		int totalSubmitted = 0;
		int underReviewed = 0;
		int reviewed = 0;
		int taskCount = 0;

		for (Task task : tasks) {
			taskCount += 1;
			List<TaskSubmission> taskSubmission = taskSubmissionRepository.getSubmittedTaskByTaskId(task.getTaskId());

			totalSubmitted += taskSubmission.size();
			for (TaskSubmission submission : taskSubmission) {
				if (submission.getStatus().equals(SubmissionStatus.Unreviewed)) {
					underReviewed += 1;
				} else if (submission.getStatus().equals(SubmissionStatus.Reviewing)
						|| submission.getStatus().equals(SubmissionStatus.Accepted)
						|| submission.getStatus().equals(SubmissionStatus.Rejected)) {
					reviewed += 1;
				}
			}

		}
		assignmentTaskStatus.setTaskCount(taskCount);
		assignmentTaskStatus.setUnReveiwed(underReviewed);
		assignmentTaskStatus.setReveiwed(reviewed);
		assignmentTaskStatus.setTotalSubmitted(totalSubmitted);
		return ResponseEntity.ok(assignmentTaskStatus);
	}

	@Override
	public ResponseEntity<?> getAllTaskOfStudent(Integer studentId) {

		Student student = studentRepository.findById(studentId).get();
		List<Task> list = new ArrayList<>();
		List<Subject> subjects = student.getCourse().getSubjects();
		subjects.forEach(obj -> {
			list.addAll(filterTasks(taskRepo.findBySubjectAndIsDeletedFalse(obj)));
		});
		//return filterTasks(list);
		return ResponseEntity.ok(null);
	}

	@Override
	public ResponseEntity<?> isTaskSubmitted(Long taskId, Integer studentId) {
		AssignmentSubmission submission = taskSubmissionRepository.findByTaskIdAndStudentId(taskId, studentId);
		if (Objects.nonNull(submission)) {
//			if (submission.getStatus().name().equals("Rejected"))
//				return new ResponseEntity<>(false, HttpStatus.OK);
//			else
			return new ResponseEntity<>(true, HttpStatus.OK);
		} else {
			return new ResponseEntity<>(false, HttpStatus.OK);
		}
	}

	public List<Task> filterTasks(List<Task> list) {

		List<Task> list2 = list.parallelStream().filter(o -> !o.getIsDeleted()).collect(Collectors.toList());
		return list2.parallelStream().filter(obj -> {
			obj.setTaskQuestion(
					obj.getTaskQuestion().parallelStream().filter(o -> !o.getIsDeleted()).collect(Collectors.toList()));
			return obj != null;
		}).collect(Collectors.toList());
	}

	public Task filterTask(Task task) {
		task.setTaskQuestion(task.getTaskQuestion().parallelStream().filter(obj -> !obj.getIsDeleted())
				.collect(Collectors.toList()));
		return task;
	}

	public TaskSubmissionResponse taskResponse(TaskSubmission submission) {

		TaskSubmissionResponse response = new TaskSubmissionResponse();
		response.setId(submission.getId());
		response.setReview(submission.getReview());
		response.setStatus(submission.getStatus().toString());
		response.setTaskDescription(submission.getTaskDescription());
		response.setTaskName(submission.getTaskName());
		response.setTaskId(submission.getTaskId());
		response.setSubmittionFileName(submission.getSubmittionFileName());
		response.setSubmissionDate(submission.getSubmissionDate());
        response.setStudentProfilePic(submission.getStudent().getProfilePic());
        response.setStudentId(submission.getStudent().getStudentId());
        response.setFullName(submission.getStudent().getFullName());
        response.setApplyForCoure(submission.getStudent().getApplyForCourse());
		return response;
	}
}
