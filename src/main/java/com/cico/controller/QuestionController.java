package com.cico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Question;
import com.cico.service.IQuestionService;

@RestController
@RequestMapping("/question")
@CrossOrigin("*")
public class QuestionController {

	@Autowired
	IQuestionService questionService;

	@PostMapping("/addQuestionToChapter")
	public ResponseEntity<Question> addQuestion(@RequestParam("chapterId") Integer chapterId,
			@RequestParam("questionContent") String questionContent, @RequestParam("option1") String option1,
			@RequestParam("option2") String option2, @RequestParam("option3") String option3,
			@RequestParam("option4") String option4,
			@RequestParam(name = "image", required = false) MultipartFile image,
			@RequestParam("correctOption") String correctOption) {
		Question question = questionService.addQuestion(chapterId, questionContent, option1, option2, option3, option4,
				image, correctOption);
		return new ResponseEntity<Question>(question, HttpStatus.OK);
	}

	@PutMapping("/updateQuestionById")
	public ResponseEntity<Question> updateQuestion(@RequestParam("questionContent") String questionContent,
			@RequestParam("option1") String option1, @RequestParam("option2") String option2,
			@RequestParam("option3") String option3, @RequestParam("option4") String option4,
			@RequestParam("questionId") Integer questionId, @RequestParam("correctOption") String correctOption,
			@RequestParam(name = "image", required = false) MultipartFile image) {
		Question updateQuestion = questionService.updateQuestion(questionId, questionContent, option1, option2, option3,
				option4, correctOption, image);
		return new ResponseEntity<Question>(updateQuestion, HttpStatus.OK);
	}

	@GetMapping("/getAllQuestionByChapterId")
	public ResponseEntity<List<Question>> getAllQuestionById(@RequestParam("chapterId") Integer chapterId) {
		List<Question> question = questionService.getAllQuestionByChapterId(chapterId);
		return ResponseEntity.ok(question);
	}

	@GetMapping("/getQuestionById")
	public ResponseEntity<Question> getQuestionById(@RequestParam("questionId") Integer questionId) {
		Question question = questionService.getQuestionById(questionId);
		return ResponseEntity.ok(question);
	}

	@PutMapping("/deleteQuestionById")
	public ResponseEntity<?> deleteQuestion(@RequestParam("questionId") Integer questionId) {
		questionService.deleteQuestion(questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping("/updateQuestionStatus")
	public ResponseEntity<?> updateQuestionStatus(@RequestParam("questionId") Integer questionId) {
		questionService.updateQuestionStatus(questionId);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping("/getAllQuestions")
	public ResponseEntity<List<Question>> getAllQuestions() {
		List<Question> questions = questionService.getAllQuestions();
		return ResponseEntity.ok(questions);
	}

	@GetMapping("/getQuestionsByExam")
	public ResponseEntity<List<Question>> getQuestionsByExam(@RequestParam("examId") Integer examId) {
		List<Question> questions = questionService.getQuestionsByExam(examId);
		return ResponseEntity.ok(questions);
	}
}
