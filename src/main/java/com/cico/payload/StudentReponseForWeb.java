package com.cico.payload;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class StudentReponseForWeb {

	private Integer studentId;
	private String userId;
	private String fullName;
	private String mobile;
	private String email;
	private LocalDate dob;
	private String profilePic;
	private String currentCourse;
	private String applyForCourse;
	private LocalDate joinDate;
	private CourseResponse courseResponse;

}
