package com.cico.payload;

import java.time.LocalDateTime;

import com.cico.util.SubmissionStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
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
	private String review;
	private String taskName;
	//ts.student.applyForCourse ,ts.student.fullName ,ts.submissionDate ,ts.status,ts.student.profilePic,a.title,ts.submitFile,ts.description,ts.submissionId
	public AssignmentSubmissionResponse( String applyForCourse,String fullName, LocalDateTime submissionDate,SubmissionStatus status 
			, String profilePic, String title, String submitFile, String description, Long submissionId,
			String review) {
		super();
		this.status = ((SubmissionStatus)status).toString();
		this.submissionDate = submissionDate;
		this.applyForCourse = applyForCourse;
		this.fullName = fullName;
		this.profilePic = profilePic;
		this.title = title;
		this.submitFile = submitFile;
		this.description = description;
		this.submissionId = submissionId;
		this.review = review;
	}

	public AssignmentSubmissionResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
