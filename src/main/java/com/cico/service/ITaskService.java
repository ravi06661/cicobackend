package com.cico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Task;
import com.cico.payload.TaskFilterRequest;
import com.cico.payload.TaskRequest;
import com.cico.util.SubmissionStatus;

public interface ITaskService {

	ResponseEntity<?> createTask(TaskRequest taskRequest);

	void updateTaskStatus(Long taskId);

	List<Task> getFilteredTasks(TaskFilterRequest taskFilter);

	ResponseEntity<?> getTaskById(Long taskId);

	List<Task> getAllTask();
	
	ResponseEntity<?> studentTaskSubmittion(Long taskId, Integer studentId, MultipartFile file, String taskDescription);

	ResponseEntity<?> addQuestionInTask(String question, String videoUrl, List<MultipartFile> questionImages,
			Long taskId);

	ResponseEntity<?> addTaskAttachment(Long taskId, MultipartFile attachment);

	ResponseEntity<?> deleteTaskQuestion( Long questionId);

	ResponseEntity<?> getAllSubmitedTasks(Integer courseId, Integer subjectId, SubmissionStatus status);

	ResponseEntity<?> getAllSubmissionTaskStatus();

	ResponseEntity<?> getSubmitedTaskForStudent(Integer studentId);

	ResponseEntity<?> updateSubmitedTaskStatus(Long submissionId, String status, String review);

	ResponseEntity<?> getOverAllTaskStatusforBarChart();

	ResponseEntity<?> getAllTaskOfStudent(Integer studentId);

	ResponseEntity<?> isTaskSubmitted(Long taskId, Integer studentId);

	ResponseEntity<?> getSubmissionTaskById(Long id);

	ResponseEntity<?> getTaskQuestion(Long questionId);

	ResponseEntity<?> getAllSubmissionTaskStatusByCourseIdAndSubjectId(Integer courseId, Integer subjectId);

	ResponseEntity<?> updateTaskQuestion(Long questionId, String question, String videoUrl, List<String> questionImages,
			List<MultipartFile> newImages);

}
