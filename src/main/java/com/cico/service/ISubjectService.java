
package com.cico.service;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.cico.model.Subject;
import com.cico.payload.SubjectResponse;


public interface ISubjectService {

	ResponseEntity<?> addSubject(String subjectName,Integer imageId);

	void addChapterToSubject(Integer subjectId, String chapterName);

	ResponseEntity<?> updateSubject(SubjectResponse subject) throws Exception;

	Map<String,Object> getSubjectById(Integer subjectId);

	void deleteSubject(Integer subjectId);

	void updateSubjectStatus(Integer subjectId);

	List<SubjectResponse> getAllSubjects();
	List<SubjectResponse> getAllSubjectsWithChapterCompletedStatus(Integer studentId);

	ResponseEntity<?> getAllChapterWithSubjectId(Integer subjectId);

//	List<SubjectResponse> getAllSubjectsByCourseId(Integer courseId);

}
