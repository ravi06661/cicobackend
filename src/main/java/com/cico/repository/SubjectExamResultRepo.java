package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cico.model.Student;
import com.cico.model.Subject;
import com.cico.model.SubjectExamResult;

@Repository
public interface SubjectExamResultRepo extends JpaRepository<SubjectExamResult, Integer> {

	Optional<SubjectExamResult> findBySubjectAndStudent(Subject subject, Student student);

	@Query("SELECT  NEW com.cico.payload.ExamResultResponse( r.id, r.correcteQuestions , r.wrongQuestions ,  r.notSelectedQuestions , r.student.profilePic,r.student.studentId ,r.student.fullName,r.scoreGet , r.totalQuestion  )FROM  SubjectExamResult r WHERE r.subject.subjectId =:subjectId ")
	List<SubjectExamResult> findAllStudentResultWithSubjectId(Integer subjectId);

//	Optional<SubjectExamResult> findByStudentIdAndSubjectId(Integer studentId, Integer subjectId);

}
