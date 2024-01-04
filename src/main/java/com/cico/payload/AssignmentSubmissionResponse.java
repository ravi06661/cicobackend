package com.cico.payload;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class AssignmentSubmissionResponse {

	private String status;
	private LocalDateTime submissionDate;
	private String applyForCourse;
	private String fullName;
	private String profilePic;
	private String title;
	private Long assginmentId;
	private Long taskId;
	private String submitFile;
	private String description;
	private Long submissionId;
}
