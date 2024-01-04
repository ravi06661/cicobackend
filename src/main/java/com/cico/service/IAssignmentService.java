package com.cico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Assignment;
import com.cico.payload.AssignmentQuestionRequest;
import com.cico.payload.AssignmentRequest;
import com.cico.payload.AssignmentSubmissionRequest;

public interface IAssignmentService {

	Assignment getAssignment(Long id);

	ResponseEntity<?> createAssignment(AssignmentRequest assignmentRequest) throws Exception;

	//ResponseEntity<?> addQuestionInAssignment(AssignmentQuestionRequest questionRequest);

	ResponseEntity<?> getAllAssignments();

	ResponseEntity<?> getAssignmentQuesById(Long questionId,Long assignmentId);

	ResponseEntity<?> submitAssignment(MultipartFile file, AssignmentSubmissionRequest readValue) throws Exception;

	ResponseEntity<?> getSubmitedAssignmetByStudentId(Integer studentId);

	ResponseEntity<?> getAllSubmitedAssginments();

	ResponseEntity<?> updateSubmitedAssignmentStatus(Long submissionId,String status,String review);

	ResponseEntity<?> addQuestionInAssignment2(String question, String videoUrl, List<MultipartFile> questionImages, Long assignmentId);

	ResponseEntity<?> deleteTaskQuestion(Long questionId);

	ResponseEntity<?> addAssignment(Long assignmentId, MultipartFile attachment);

	ResponseEntity<?> getAllSubmissionAssignmentTaskStatus();

	ResponseEntity<?> getOverAllAssignmentTaskStatus();

	ResponseEntity<?> getAllLockedAndUnlockedAssignment(Integer studentId);

	ResponseEntity<?> getAssignmentQuesSubmissionStatus(Long questionId, Long assignmentId, Integer studentId);

	ResponseEntity<?> getAllSubmissionAssignmentTaskStatusByCourseIdAndSubjectId(Integer courseId,Integer subjectId);

}

