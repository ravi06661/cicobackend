package com.cico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Question;

public interface IQuestionService {

	Question addQuestionToChapterExam(Integer chapterId,String questionContent,String option1,String option2, String option3,String option4,MultipartFile image, String correctOption);

	ResponseEntity<?> updateQuestion(Integer questionId,String questionContent,String option1,String option2, String option3,String option4, String correctOption,MultipartFile image);

	List<Question> getAllQuestionByChapterId(Integer chapterId);

	void deleteQuestion(Integer questionId);

	void updateQuestionStatus(Integer questionId);

	List<Question> getAllQuestions();

	List<Question> getQuestionsByExam(Integer examId);

	Question getQuestionById(Integer questionId); //running

	Question addQuestionToSubjectExam(Integer subjectId, String questionContent, String option1, String option2,
			String option3, String option4, MultipartFile image, String correctOption);

	List<Question> getAllQuestionBySubjectId(Integer subjectId);

	ResponseEntity<?> getAllSubjectQuestionForTest(Integer subjectId);

	ResponseEntity<?> getAllSubjectExam(Integer studentId);

}
