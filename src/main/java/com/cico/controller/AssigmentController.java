package com.cico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Assignment;
import com.cico.payload.AssignmentRequest;
import com.cico.payload.AssignmentSubmissionRequest;
import com.cico.service.IAssignmentService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/assignment")
@CrossOrigin("*")
public class AssigmentController {

	@Autowired
	private IAssignmentService service;

	@Autowired
	private ObjectMapper objectMapper;

	@PostMapping("/createAssignment")
	public ResponseEntity<?> createAssignment(@RequestBody AssignmentRequest assignmentRequest) throws Exception {
		return service.createAssignment(assignmentRequest);
	}

	@PostMapping("/addAssignment")
	public ResponseEntity<?> addAssignment(@RequestParam("assignmentId") Long assignmentId,
			@RequestParam("attachment") MultipartFile attachment) {
		return this.service.addAssignment(assignmentId, attachment);
	}

	@GetMapping("/getAssignment")
	public ResponseEntity<Assignment> getAssigment(@RequestParam("assignmentId") Long id) {
		Assignment assignment = service.getAssignment(id);
		return ResponseEntity.ok(assignment);
	}

	@PostMapping("/addQuestionInAssignment")
	public ResponseEntity<?> addQuestionInAssignment(@RequestParam("assignmentId") Long assignmentId,
			@RequestParam("question") String question, @RequestParam("videoUrl") String videoUrl,
			@RequestParam("questionImages") List<MultipartFile> questionImages) {
		return service.addQuestionInAssignment2(question, videoUrl, questionImages, assignmentId);
	}

	@GetMapping("/getAllAssignments")
	public ResponseEntity<?> getAllAssignments() {
		return service.getAllAssignments();
	}

	@GetMapping("/getAssignmentQuesById")
	public ResponseEntity<?> getAssignmentQuestion(@RequestParam("questionId") Long questionId,
			@RequestParam("assignmentId") Long assignmentId) {
		return service.getAssignmentQuesById(questionId, assignmentId);
	}

	@DeleteMapping("/deleteTaskQuestion")
	public ResponseEntity<?> deleteTaskQuestions(@RequestParam("questionId") Long questionId) {
		return service.deleteTaskQuestion(questionId);
	}

	@PostMapping("/submitAssignment")
	public ResponseEntity<?> submitAssignmentByStudent(@RequestParam("file") MultipartFile file,
			@RequestParam("assignmentSubmissionRequest") String assignmentSubmissionRequest)
			throws Exception {
		AssignmentSubmissionRequest readValue = objectMapper.readValue(assignmentSubmissionRequest,
				AssignmentSubmissionRequest.class);
		return service.submitAssignment(file, readValue);
	}

	// This API for student Uses
	@GetMapping("/getSubmitedAssignmetByStudentId")
	public ResponseEntity<?> getSubmitedAssignmetByStudentId(@RequestParam("studentId") Integer studentId) {
		return service.getSubmitedAssignmetByStudentId(studentId);
	}

	// This API for Admin Uses
	@GetMapping("/getAllSubmitedAssginments")
	public ResponseEntity<?> getAllSubmitedAssginments() {
		return service.getAllSubmitedAssginments();
	}

	@PutMapping("/updateSubmitedAssignmentStatus")
	public ResponseEntity<?> updateSubmitedAssignmentStatus(@RequestParam("submissionId") Long submissionId,
			@RequestParam("status") String status, @RequestParam("review") String review) {
		return service.updateSubmitedAssignmentStatus(submissionId, status, review);
	}

	@GetMapping("/getAllSubmissionAssignmentTaskStatus")
	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatus() {
		return service.getAllSubmissionAssignmentTaskStatus();
	}

	@GetMapping("getOverAllAssignmentTaskStatus")
	public ResponseEntity<?> getOverAllAssignmentTaskStatus() {
		return service.getOverAllAssignmentTaskStatus();
	}

	@GetMapping("/getAllLockedAndUnlockedAssignment")
	public ResponseEntity<?> getAllLockedAndUnlockedAssignment(@RequestParam("studentId")Integer studentId) {
		return service.getAllLockedAndUnlockedAssignment(studentId);
	}
	
	@GetMapping("/getAssignmentQuesSubmissionStatus")
	public ResponseEntity<?> getAssignmentQuesSubmissionStatus(@RequestParam("questionId") Long questionId,
			@RequestParam("assignmentId") Long assignmentId,@RequestParam("studentId")Integer studentId) {
		return service.getAssignmentQuesSubmissionStatus(questionId, assignmentId,studentId);
	}
	
	@GetMapping("/getAllSubmissionAssignmentTaskStatusByCourseIdFilter")
	public ResponseEntity<?> getAllSubmissionAssignmentTaskStatusByCourseId(@RequestParam("courseId") Integer courseId,@RequestParam("subjectId") Integer subjectId)
	{
		return service.getAllSubmissionAssignmentTaskStatusByCourseIdAndSubjectId(courseId,subjectId);
	}
	
}
