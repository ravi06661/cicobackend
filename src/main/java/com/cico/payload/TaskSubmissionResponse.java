package com.cico.payload;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class TaskSubmissionResponse {

	private Integer id;
	private String taskDescription;
	private String submittionFileName;
	private Long taskId;
	private LocalDateTime submissionDate;
	private String status;
	private String review;
	private String taskName;
	private String studentProfilePic;
	private Integer studentId;
	private String fullName;
	private String applyForCoure;

}
