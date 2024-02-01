package com.cico.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class TaskResponse {
	private Long taskId;
	private String taskName;
	private CourseResponse course;
	private SubjectResponse subject;
	private List<TaskQuestionResponse> taskQuestion;
	private String taskAttachment;
	private String attachmentStatus;
	public TaskResponse(Long taskId, String taskName) {
		super();
		this.taskId = taskId;
		this.taskName = taskName;
	}
	
}
