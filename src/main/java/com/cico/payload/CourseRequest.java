package com.cico.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseRequest {
	private String courseName;
	private String courseFees;
	private String duration;
	private String sortDescription;
	private Integer technologyStack;
	private Integer subjectIds[];
	private Boolean isStarterCourse;
}
