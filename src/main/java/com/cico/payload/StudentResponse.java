package com.cico.payload;

import java.time.LocalDate;

import com.cico.model.Course;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StudentResponse {
	private Integer studentId;
	private String userId;
	private	String fullName;
	private String mobile;
	private String email;
	private LocalDate dob;
	private String inUseDeviceId;
	private String profilePic;
	private String deviceId;
	private String fcmId;
	private String currentCourse;
	private Course course;
		
	public StudentResponse(Integer studentId, String userId, String fullName, String mobile, String email,
			LocalDate dob, String inUseDeviceId, String profilePic, String deviceId, String fcmId, String deviceType) {
		super();
		this.studentId = studentId;
		this.userId = userId;
		this.fullName = fullName;
		this.mobile = mobile;
		this.email = email;
		this.dob = dob;
		this.inUseDeviceId = inUseDeviceId;
		this.profilePic = profilePic;
		this.deviceId = deviceId;
		this.fcmId = fcmId;
		this.deviceType = deviceType;
	}
	private String deviceType;
	private LocalDate joinDate;
	private String applyForCourse;
	private Boolean completionDuration=false;

}
