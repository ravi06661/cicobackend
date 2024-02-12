package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cico.model.Subject;
import com.cico.payload.SubjectExamResponse;
import com.cico.payload.SubjectResponse;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {


	@Query("SELECT s FROM Subject s  WHERE s.subjectName = :subjectName AND s.isDeleted = 0 ")
	Subject findBySubjectNameAndIsDeleted(@Param("subjectName") String subjectName);

	@Query("SELECT s FROM Subject s  WHERE s.subjectId = :subjectId AND s.isDeleted=0  ")
	Optional<Subject> findBySubjectIdAndIsDeleted(@Param("subjectId") Integer subjectId);

	@Query("SELECT s.subjectId,s.subjectName, s.technologyStack.technologyName, s.technologyStack.imageName, COUNT( DISTINCT c) ,s.technologyStack.id "
			+ "FROM Subject s " + "LEFT JOIN s.chapters c ON c.isDeleted = 0 " + "WHERE s.isDeleted = 0 "
			+ "GROUP BY s.subjectId")
	List<Object[]> findByIsDeletedFalse();

	@Query("SELECT  s.subjectId ,s.technologyStack.imageName ,s.subjectName,c.chapterId  , c.chapterName FROM Subject s   LEFT JOIN s.chapters  c ON c.isDeleted = 0      WHERE  s.isDeleted =0  AND  s.subjectId =:subjectId ")
	List<Object[]> getAllChapterWithSubjectId(Integer subjectId);

	@Query("SELECT NEW com.cico.payload.SubjectResponse(s.subjectId, s.subjectName, s.technologyStack.id, s.technologyStack.technologyName, s.technologyStack.imageName) " +
		       "FROM Course c " +
		       "JOIN c.subjects s ON s.isDeleted = 0 AND s.isActive = 1" +
		       "WHERE c.courseId = :courseId AND c.isDeleted =0 AND s.isActive = 1")
		List<SubjectResponse> getAllSubjectByCourseId(@Param("courseId") Integer courseId);
	
	@Query("SELECT s.subjectId, s.technologyStack.imageName, s.subjectName, c.chapterId, c.chapterName, er.scoreGet " +
	        "FROM Subject s " +
	        "LEFT JOIN s.chapters c ON c.isDeleted = 0 " +
	        "LEFT JOIN ChapterExamResult er ON (er.chapter.chapterId = c.chapterId AND er.student.studentId = :studentId) " +
	        "WHERE s.isDeleted = 0 AND s.subjectId = :subjectId ")
	List<Object[]> getAllChapterWithSubjectIdAndStudentId(@Param("subjectId") Integer subjectId, @Param("studentId") Integer studentId);
	
	@Query("SELECT NEW com.cico.payload.SubjectExamResponse( s.subjectName,  e.examId,s.subjectId, s.technologyStack.imageName, e.isActive, e.examTimer, COUNT(q), e.passingMarks,sr.scoreGet ,e.startTimer) " +
		       "FROM Course c " +
		       "LEFT JOIN c.subjects s  ON s.isDeleted = 0 " +
		       "LEFT JOIN s.exam e " +
		       "LEFT JOIN e.questions q ON q.isDeleted = 0 " +
		       "LEFT JOIN SubjectExamResult  sr ON sr.student.studentId =:studentId " +
		       "JOIN Student student ON student.studentId = :studentId " +
		       "WHERE student.course = c AND c.isDeleted = 0  " +
		       "GROUP BY s.subjectId, e.examId ,sr.id")
		List<SubjectExamResponse> getAllSubjectExam(@Param("studentId") Integer studentId);

}
