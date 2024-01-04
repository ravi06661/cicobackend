package com.cico.repository;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cico.model.AssignmentSubmission;
import com.cico.util.SubmissionStatus;

public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

//	@Query("SELECT a.review, a.status, a.submissionDate, a.submitFile, a.description, a.taskId, a.assignmentId, a1.title FROM AssignmentSubmission a INNER JOIN Assignment a1 ON a1.id = a.assignmentId WHERE a.student.studentId = :studentId ORDER BY a.submissionDate ASC")
//	List<Object[]> getSubmitAssignmentByStudentId(@Param("studentId") Integer studentId);
//	@Query("SELECT a.review, a.status, a.submissionDate, a.submitFile, a.description, a1.title ,a1.id,a2.questionId,"
//			+ "FROM AssignmentSubmission a INNER JOIN Assignment a1 ON a1.id = a.assignmentId "
//			+ "WHERE a.student.studentId = :studentId "
//			+ "ORDER BY a.submissionDate ASC, FUNCTION('TIME_FORMAT', a.submissionDate, 'HH:mm:ss') ASC")
//	List<Object[]> getSubmitAssignmentByStudentId(@Param("studentId") Integer studentId);
//

//	@Query("SELECT a FROM AssignmentSubmission a WHERE  a.questionId =:questionId")
//	List<AssignmentSubmission> getSubmitAssignmentByQuestionId(@Param("questionId") Long questionId);
//
	@Transactional
	@Modifying
	@Query("UPDATE AssignmentSubmission a SET a.status=:status , a.review=:review WHERE a.submissionId=:submissionId")
	void updateSubmitAssignmentStatus(@Param("submissionId") Long submissionId,
			@Param("status") SubmissionStatus status, @Param("review") String review);
//
//	@Query("SELECT  a FROM AssignmentSubmission a  WHERE a.assignmentId = :id AND a.student.studentId =:studentId")
//	List<AssignmentSubmission> findByAssignmentIdAndStudentId(@Param("id") Long id,
//			@Param("studentId") Integer studentId);
//
//	@Query("SELECT a FROM AssignmentSubmission a WHERE a.assignmentId = :assignmentId AND a.taskId = :taskId AND a.student.studentId =:studentId")
//	AssignmentSubmission findByAssignmentIdAndQuestionIdAndStudentId(@Param("assignmentId") Long assignmentId,
//			@Param("taskId") Long taskId, @Param("studentId") Integer studentId);
//
//	@Query("SELECT COUNT(*)   FROM  AssignmentSubmission a WHERE a.assignmentId =:id  AND a.student.studentId =:studentId ")
//	Long getTotalCompletedTask(Long id, Integer studentId);

	@Query("SELECT a FROM AssignmentSubmission a WHERE  a.submissionId =:submissionId")
	Optional<AssignmentSubmission> findBySubmissionId(Long submissionId);
}
