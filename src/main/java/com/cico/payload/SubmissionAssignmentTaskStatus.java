package com.cico.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class SubmissionAssignmentTaskStatus {

	private Long assignmentId;
	private String taskTitle;
	private Long taskId;
	private Integer totalSubmitted;
	private Integer unReveiwed;
	private boolean status;
	private Integer reveiwed;
	private Integer taskCount;
	private String assignmentTitle;
	private String description;
}
