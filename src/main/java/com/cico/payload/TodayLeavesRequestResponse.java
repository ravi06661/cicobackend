package com.cico.payload;

import java.time.LocalDate;

import lombok.Data;

@Data
public class TodayLeavesRequestResponse {

	private Integer studentId;
	private String fullName;
	private String applyForCourse;
	private String profilePic;
	private String leaveReason;
	private LocalDate leaveDate;
	private LocalDate leaveEndDate;
	private Integer leaveDuration;
	private Integer leaveTypeId;
    private Integer leaveId;
    private String leaveTypeName;
}
