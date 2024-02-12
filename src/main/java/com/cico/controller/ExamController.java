package com.cico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Exam;
import com.cico.payload.ExamRequest;
import com.cico.service.IExamService;

@RestController
@RequestMapping("/exam")
@CrossOrigin("*")
public class ExamController {

	@Autowired
	private IExamService examService;

	@PostMapping("/addExam")
	public ResponseEntity<String> addExam(@RequestParam("examName") String examName) {
//		examService.addExam(examName);	
		return ResponseEntity.ok("Exam Added");
	}

	@PostMapping("/addQuestionsToExam")
	public ResponseEntity<String> addQuestionsToExam(@RequestParam("examId") Integer examId,
			@RequestParam("examId") String question, @RequestParam("options") List<String> options,
			@RequestParam(name = "image",required = false ) MultipartFile image) {
		examService.addQuestionsToExam(examId, question, options, image);
		return ResponseEntity.ok("Questions Added");
	}

	@PutMapping("/updateExam")
	public ResponseEntity<String> updateExam(@RequestBody Exam exam) {
		examService.updateExam(exam);
		return ResponseEntity.ok("Exam Updated");
	}

	@GetMapping("/getExamById")
	public ResponseEntity<Exam> getExamById(@RequestParam("examId") Integer examId) {
		Exam exam = examService.getExamById(examId);
		return ResponseEntity.ok(exam);
	}

	@PutMapping("/deleteExam")
	public ResponseEntity<String> deleteExam(@RequestParam("examId") Integer examId) {
		examService.deleteExam(examId);
		return ResponseEntity.ok("Exam Deleted");
	}

	@PutMapping("/updateExamStatus")
	public ResponseEntity<String> updateExamStatus(@RequestParam("examId") Integer examId) {
		examService.updateExamStatus(examId);
		return ResponseEntity.ok("Exam Updated");
	}

	@GetMapping("/getAllExams")
	public ResponseEntity<List<Exam>> getAllExams() {
		List<Exam> exams = examService.getAllExams();
		return ResponseEntity.ok(exams);
	}

//	@GetMapping("/getExamsByChapter")
//	public ResponseEntity<List<Exam>> getExamsByChapter(@RequestParam("chapterId") Integer chapterId) {
//		List<Exam> exams = examService.getExamsByChapter(chapterId);
//		return ResponseEntity.ok(exams);
//	}

	@PostMapping("/addChapterExam")
	public ResponseEntity<?> addChapterExamResult(@RequestBody ExamRequest chapterExamResult) {
		return this.examService.addChapterExamResult(chapterExamResult);
	}
	@PostMapping("/addSubjectExam")
	public ResponseEntity<?> addSubjectExamResult(@RequestBody ExamRequest chapterExamResult) {
		return this.examService.addSubjectExamResult(chapterExamResult);
	}
	
	@GetMapping("/getChapterExamResult")
	public ResponseEntity<?> getChapterExamResult(@RequestParam("resultId") Integer id){
		return examService.getChapterExamResult(id);
	}
	
	@GetMapping("/getSubjectExamResult")
	public ResponseEntity<?> getSubjectExamResult(@RequestParam("resultId") Integer id){  ///
		return examService.getSubjectExamResult(id);
	}
	
	@GetMapping("/getALLChapterExamResultesByChapterIdApi")
	public ResponseEntity<?> getChapterExamResultes(@RequestParam("chapterId") Integer chapterId){
		return examService.getChapterExamResultByChaterId(chapterId);
	}
	
	@GetMapping("/getALLSubjectExamResultesBySubjectIddApi")
	public ResponseEntity<?> getSubjectExamResultes(@RequestParam("subjectId") Integer subjectId){
		return examService.getSubjectExamResultesBySubjectId(subjectId);
	}
	
	@GetMapping("/checkExamCompleteOrNot")
	public ResponseEntity<?> checkExamCompletedOrNot(@RequestParam("chapterId") Integer chapterId,@RequestParam("studentId") Integer studentId){
		return examService.getChapterExamIsCompleteOrNot(chapterId,studentId);
	}
	

	@GetMapping("/getSubectExamIsCompleteOrNot")
	public ResponseEntity<?> checkSubjectCompleteOrNot(@RequestParam("subjectId") Integer subjectId,@RequestParam("studentId") Integer studentId){
		return examService.getSubjectExamIsCompleteOrNot(subjectId,studentId);
	}
}
