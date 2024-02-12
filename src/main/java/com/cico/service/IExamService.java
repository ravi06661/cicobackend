package com.cico.service;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import com.cico.model.Exam;
import com.cico.payload.ExamRequest;

public interface IExamService {

	// void addExam(String examName);

	void addQuestionsToExam(Integer examId, String question, List<String> options, MultipartFile image);

	void updateExam(Exam exam);

	Exam getExamById(Integer examId);

	void deleteExam(Integer examId);

	void updateExamStatus(Integer examId);

	List<Exam> getAllExams();

	ResponseEntity<?> addChapterExamResult(ExamRequest chapterExamResult);

	ResponseEntity<?> getChapterExamResult(Integer id);

	ResponseEntity<?> getChapterExamIsCompleteOrNot(Integer chapterId, Integer studentId);

	ResponseEntity<?> getChapterExamResultByChaterId(Integer chapterId);

	ResponseEntity<?> getSubjectExamIsCompleteOrNot(Integer subjectId, Integer studentId);

	ResponseEntity<?> getSubjectExamResult(Integer subjectId);

	ResponseEntity<?> addSubjectExamResult(ExamRequest request);

	ResponseEntity<?> getSubjectExamResultesBySubjectId(Integer subjectId);

}
