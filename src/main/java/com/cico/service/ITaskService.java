package com.cico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Task;
import com.cico.payload.TaskFilterRequest;
import com.cico.payload.TaskRequest;

public interface ITaskService {

	Task createTask(TaskRequest taskRequest);

	void updateTaskStatus(Long taskId);

	List<Task> getFilteredTasks(TaskFilterRequest taskFilter);

	Task getTaskById(Long taskId);

	List<Task> getAllTask();
	
	ResponseEntity<?> studentTaskSubmittion(Long taskId, Integer studentId, MultipartFile file, String taskDescription) throws Exception;

	ResponseEntity<?> addQuestionInTask(String question, String videoUrl, List<MultipartFile> questionImages,
			Long taskId);

	ResponseEntity<?> addTaskAttachment(Long taskId, MultipartFile attachment);

	ResponseEntity<?> deleteTaskQuestion( Long questionId);

	ResponseEntity<?> getAllSubmitedTasks();

	ResponseEntity<?> getAllSubmissionTaskStatus();

	ResponseEntity<?> getSubmitedTaskForStudent(Integer studentId);

	ResponseEntity<?> updateSubmitedTaskStatus(Integer submissionId, String status, String review);

	ResponseEntity<?> getOverAllTaskStatusforBarChart();

	List<Task> getAllTaskOfStudent(Integer studentId);

	ResponseEntity<?> isTaskSubmitted(Long taskId, Integer studentId);

}
