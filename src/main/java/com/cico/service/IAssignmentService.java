package com.cico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Assignment;
import com.cico.payload.AssignmentRequest;
import com.cico.payload.AssignmentSubmissionRequest;
import com.cico.util.SubmissionStatus;

public interface IAssignmentService {

	ResponseEntity<?> getAssignment(Long id);

	ResponseEntity<?> createAssignment(AssignmentRequest assignmentRequest) throws Exception;

	// ResponseEntity<?> addQuestionInAssignment(AssignmentQuestionRequest
	// questionRequest);

	ResponseEntity<?> getAllAssignments();

	ResponseEntity<?> getAssignmentQuesById(Long questionId);

	ResponseEntity<?> submitAssignment(MultipartFile file, AssignmentSubmissionRequest readValue) throws Exception;

	ResponseEntity<?> getSubmitedAssignmetByStudentId(Integer studentId);

	ResponseEntity<?> getAllSubmitedAssginments(Integer courseId, Integer subjectId, SubmissionStatus status);

	ResponseEntity<?> updateSubmitedAssignmentStatus(Long submissionId, String status, String review);

	ResponseEntity<?> addQuestionInAssignment(String question, String videoUrl, List<MultipartFile> questionImages,
			Long assignmentId);

	ResponseEntity<?> deleteTaskQuestion(Long questionId);

	ResponseEntity<?> addAssignment(Long assignmentId, MultipartFile attachment);

	ResponseEntity<?> getAllSubmissionAssignmentTaskStatus();

	ResponseEntity<?> getOverAllAssignmentTaskStatus();

	ResponseEntity<?> getAllLockedAndUnlockedAssignment(Integer studentId);

	ResponseEntity<?> getAssignmentQuesSubmissionStatus(Long questionId, Integer studentId);

	ResponseEntity<?> getAllSubmissionAssignmentTaskStatusByCourseIdAndSubjectId(Integer courseId, Integer subjectId);

	ResponseEntity<?> getSubmittedAssignmentBySubmissionId(Long submissionId);

	ResponseEntity<?> updateAssignmentQuestion(Long questionId, String question, String videoUrl,
			List<String> questionImages, List<MultipartFile> newImages);

}
