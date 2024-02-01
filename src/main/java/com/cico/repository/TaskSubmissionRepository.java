package com.cico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cico.model.TaskSubmission;
import com.cico.util.SubmissionStatus;

@Repository
public interface TaskSubmissionRepository extends JpaRepository<TaskSubmission, Long> {

	@Query("SELECT ts.review, ts.status, ts.submissionDate, ts.submittionFileName, ts.taskDescription, t.taskId, t.taskName "
			+ "FROM Task t JOIN t.assignmentSubmissions ts " + "WHERE ts.student.studentId = :studentId "
			+ "ORDER BY ts.submissionDate DESC")
	List<Object[]> getSubmitedTaskForStudent(@Param("studentId") Integer studentId);

	@Transactional
	@Modifying
	@Query("UPDATE TaskSubmission a SET a.status=:status , a.review=:review WHERE a.id=:id")
	void updateSubmitTaskStatus(@Param("id") Long submissionId, @Param("status") SubmissionStatus status,
			@Param("review") String review);

	@Query("SELECT s FROM TaskSubmission s WHERE s.id=:id")
	TaskSubmission findBySubmissionId(@Param("id") Long submissionId);

	@Query("SELECT ts FROM Task t RIGHT JOIN  t.assignmentSubmissions ts  WHERE ts.student.studentId =:studentId AND  t.taskId =:taskId")
	TaskSubmission findByTaskIdAndStudentId(@Param("taskId") Long taskId, @Param("studentId") Integer studentId);

}
