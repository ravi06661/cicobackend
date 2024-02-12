package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cico.model.Subject;
import com.cico.model.Task;
import com.cico.payload.AssignmentAndTaskSubmission;
import com.cico.payload.AssignmentSubmissionResponse;
import com.cico.payload.TaskStatusSummary;
import com.cico.util.SubmissionStatus;

public interface TaskRepo extends JpaRepository<Task, Long> {

	Task findByTaskNameAndIsDeleted(String taskName, boolean b);

	Object findByTaskName(String taskName);

	Optional<Task> findByTaskIdAndIsDeleted(Long taskId, boolean b);
  
	
	List<Task> findByIsDeletedFalse();

    List<Task> findBySubjectAndIsDeletedFalseAndIsActiveTrue(Subject subject);

	Optional<Task> findBySubjectAndIsDeletedFalse(Long taskId);

	Optional<Task> findByTaskIdAndIsDeletedFalse(Long taskId);
     
	
	

	
	@Query("SELECT  NEW com.cico.payload.AssignmentAndTaskSubmission( t.id, "
	        + "COUNT(DISTINCT ts), "
	        + "COUNT(DISTINCT CASE WHEN ts.status = 'Unreviewed' THEN ts END), "
	        + "COUNT(DISTINCT CASE WHEN ts.status IN ('Rejected' , 'Accepted' ,'Reviewing' ) THEN ts END), "
	        + "t.taskName ,t.isActive) " 
	        + "FROM Task t "
	        + "LEFT JOIN t.assignmentSubmissions ts "
	        + "WHERE (t.course.courseId = :courseId OR :courseId = 0) AND (t.subject.subjectId = :subjectId OR :subjectId = 0) "
	        + "AND t.isDeleted = false "
	        + "GROUP BY t.id  ")
	Page<AssignmentAndTaskSubmission> findAllTaskStatusWithCourseIdAndSubjectId(@Param("courseId") Integer courseId,
			@Param("subjectId") Integer subjectId, PageRequest pageRequest);
	

	
	@Query("SELECT "   
	        + "NEW com.cico.payload.TaskStatusSummary(  COUNT(ts) as totalCount, COUNT(CASE WHEN ts.status IN ('Rejected', 'Accepted', 'Reviewing') THEN ts END) as reviewedCount,COUNT(CASE WHEN ts.status = 'Unreviewed' THEN ts END) as unreviewedCount) "
	        + "FROM Task a "
	        + " JOIN a.assignmentSubmissions ts ") 
	TaskStatusSummary getOverAllTaskQuestionStatus();

	@Query("SELECT NEW com.cico.payload.AssignmentAndTaskSubmission(" +
	        "t.taskName, " + 
	        "t.id, " + 
	        "COUNT(DISTINCT ts), " +
	        "COUNT(CASE WHEN ts.status = 'Unreviewed' THEN ts END) as unreviewedCount, " + 
	        "COUNT(CASE WHEN ts.status IN ('Rejected', 'Accepted', 'Reviewing') THEN ts END) as reviewedCount) " +
	        "FROM Task t LEFT JOIN t.assignmentSubmissions ts GROUP BY  t.id " )
	 Page<AssignmentAndTaskSubmission> getAllSubmissionTaskStatus(PageRequest pageRequest);

	
	
	
//	@Query("SELECT  "
//			+ " NEW com.cico.payload.AssignmentSubmissionResponse(ts.student.applyForCourse ,ts.student.fullName ,ts.submissionDate ,ts.status,ts.student.profilePic,t.taskName,ts.submittionFileName,ts.taskDescription,ts.id,ts.review) "
//			+ "FROM Task t " + " JOIN t.assignmentSubmissions ts "
//			+ "WHERE (t.course.courseId = :courseId OR :courseId = 0) AND (t.subject.subjectId = :subjectId OR :subjectId = 0) AND (ts.status = :status OR :status = 'NOT_CHECKED_WITH_IT') " + "AND t.isDeleted = 0 GROUP BY t.id ORDER BY ts.submissionDate DESC " )
	
	@Query("SELECT "
	        + " NEW com.cico.payload.AssignmentSubmissionResponse(ts.student.applyForCourse, ts.student.fullName, ts.submissionDate, ts.status, ts.student.profilePic, t.taskName, ts.submittionFileName, ts.taskDescription, ts.id, ts.review) "
	        + " FROM Task t JOIN t.assignmentSubmissions ts "
	        + " WHERE (t.course.courseId = :courseId OR :courseId = 0) AND (t.subject.subjectId = :subjectId OR :subjectId = 0) AND (ts.status = :status OR :status = 'NOT_CHECKED_WITH_IT') "
	        + " AND t.isDeleted = 0 "
	        + " GROUP BY ts.submissionDate, ts.id ,t.id "
	        + " ORDER BY CASE WHEN ts.status = 'Unreviewed' THEN 0 ELSE 1 END, ts.status, MAX(ts.submissionDate) DESC, ts.id, t.id")
	Page<AssignmentSubmissionResponse> findAllSubmissionTaskWithCourseIdAndSubjectId(@Param("courseId") Integer courseId,
			@Param("subjectId") Integer subjectId,  @Param("status") SubmissionStatus status, PageRequest pageRequest);

}
