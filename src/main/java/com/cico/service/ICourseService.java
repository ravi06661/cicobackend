package com.cico.service;

import org.springframework.http.ResponseEntity;

import com.cico.payload.ApiResponse;
import com.cico.payload.CourseRequest;
import com.cico.payload.CourseResponse;


public interface ICourseService {

	public ResponseEntity<?> createCourse(CourseRequest request);

	public CourseResponse findCourseById(Integer courseId);

	public ResponseEntity<?> getAllCourses(Integer page, Integer size);

	public ApiResponse updateCourse(CourseResponse course);

	public Boolean deleteCourseById(Integer courseId);

	public ApiResponse studentUpgradeCourse(Integer studnetId, Integer courseId);
	
	public ResponseEntity<?> getCourseProgress(Integer studentId);

	public ResponseEntity<?> getCoureWithBatchesAndSubjects(Integer courseId);

	public ResponseEntity<?> getAllNonStarterCourses();

}
