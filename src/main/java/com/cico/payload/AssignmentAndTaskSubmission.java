package com.cico.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(value = Include.NON_NULL)
@NoArgsConstructor

public class AssignmentAndTaskSubmission {

	private Long assignmentId;
	private String taskTitle;
	private Long taskId;
	private Long totalSubmitted;
	private Long unReveiwed;
	private boolean status;
	private Long reveiwed;
	private Long taskCount;
	private String assignmentTitle;
	private String description;

	public AssignmentAndTaskSubmission(String taskTitle, Long taskId, Long totalSubmitted, Long unReveiwed,
			Long reveiwed) {
		super();
		this.taskTitle = taskTitle;
		this.taskId = taskId;
		this.totalSubmitted = totalSubmitted;
		this.unReveiwed = unReveiwed;
		this.reveiwed = reveiwed;
	}

}
