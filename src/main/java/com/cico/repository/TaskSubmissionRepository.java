package com.cico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cico.model.AssignmentSubmission;
import com.cico.model.TaskSubmission;
import com.cico.util.SubmissionStatus;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Integer> {

	@Query("SELECT a FROM TaskSubmission a WHERE a.taskId =:taskId")
	List<TaskSubmission> getSubmittedTaskByTaskId(@Param("taskId") Long taskId);

	@Query("SELECT a.review, a.status, a.submissionDate, a.submittionFileName, a.taskDescription, a.taskId, a1.taskName "
			+ "FROM TaskSubmission a INNER JOIN Task a1 ON a1.taskId = a.taskId "
			+ "WHERE a.student.studentId = :studentId "
			+ "ORDER BY a.submissionDate ASC, FUNCTION('TIME_FORMAT', a.submissionDate, 'HH:mm:ss') ASC")
	List<Object[]> getSubmitedTaskForStudent(@Param("studentId") Integer studentId);

	@Transactional
	@Modifying
	@Query("UPDATE TaskSubmission a SET a.status=:status , a.review=:review WHERE a.id=:id")
	void updateSubmitTaskStatus(@Param("id") Integer submissionId, @Param("status") SubmissionStatus status,
			@Param("review") String review);

	@Query("SELECT s FROM TaskSubmission s WHERE s.id=:id")
	TaskSubmission findBySubmissionId(@Param("id") Integer submissionId);

	@Query("SELECT a FROM TaskSubmission a WHERE a.taskId =:taskId AND a.student.studentId =:studentId")
	AssignmentSubmission findByTaskIdAndStudentId(@Param("taskId") Long taskId, @Param("studentId") Integer studentId);
}
