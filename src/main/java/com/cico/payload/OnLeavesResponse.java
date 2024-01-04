package com.cico.payload;

import java.time.LocalDate;

import lombok.Data;

@Data
public class OnLeavesResponse {
 
	private String profilePic;
	private LocalDate leaveDate;
	private LocalDate leaveEndDate;
	private String applyForCourse;
	private String name;
	private Integer studentId;
}
