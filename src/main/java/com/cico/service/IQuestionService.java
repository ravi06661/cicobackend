package com.cico.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Question;

public interface IQuestionService {

	Question addQuestion(Integer chapterId,String questionContent,String option1,String option2, String option3,String option4,MultipartFile image, String correctOption);

	Question updateQuestion(Integer questionId,String questionContent,String option1,String option2, String option3,String option4, String correctOption,MultipartFile image);

	List<Question> getAllQuestionByChapterId(Integer chapterId);

	void deleteQuestion(Integer questionId);

	void updateQuestionStatus(Integer questionId);

	List<Question> getAllQuestions();

	List<Question> getQuestionsByExam(Integer examId);

	Question getQuestionById(Integer questionId); //running

}
