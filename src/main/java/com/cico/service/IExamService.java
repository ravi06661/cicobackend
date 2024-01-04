package com.cico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Exam;
import com.cico.payload.ChapterExamResultRequest;

public interface IExamService {

	// void addExam(String examName);

	void addQuestionsToExam(Integer examId, String question, List<String> options, MultipartFile image);

	void updateExam(Exam exam);

	Exam getExamById(Integer examId);

	void deleteExam(Integer examId);

	void updateExamStatus(Integer examId);

	List<Exam> getAllExams();

	List<Exam> getExamsByChapter(Integer chapterId);

	ResponseEntity<?> addChapterExamResult(ChapterExamResultRequest chapterExamResult);

	ResponseEntity<?> getChapterExamResult(Integer id);

	ResponseEntity<?> getChapterExamIsCompleteOrNot(Integer chapterId, Integer studentId);

	ResponseEntity<?> getChapterExamResultByChaterId(Integer chapterId);

}
