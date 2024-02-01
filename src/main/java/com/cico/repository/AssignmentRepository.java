package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cico.model.Assignment;
import com.cico.model.Course;
import com.cico.payload.AssignmentSubmissionResponse;
import com.cico.payload.TaskStatusSummary;
import com.cico.util.SubmissionStatus;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

	Optional<Assignment> findByIdAndIsDeleted(Long id, boolean b);

	List<Assignment> findByIsDeletedFalse();

	Course findByCourse(Optional<Course> findByCourseId);

	@Query("SELECT a FROM Assignment a WHERE a.course.courseId = :courseId")
	List<Assignment> findAllByCourseIdAndIsDeletedFalse(@Param("courseId") Integer courseId);

	@Query("SELECT a FROM Assignment a WHERE a.course.courseId = :courseId  AND a.subject.subjectId =:subjectId")
	List<Assignment> findAllByCourseIdAndSubjectIdAndIsDeletedFalse(@Param("courseId") Integer courseId,
			@Param("subjectId") Integer subjectId);

	@Query("SELECT a FROM Assignment a WHERE a.title =:title")
	Optional<Assignment> findByName(@Param("title") String title);

	
	@Query("SELECT  t.id, " + "COUNT(DISTINCT ts), "
			+ "COUNT(DISTINCT CASE WHEN ts.status = 'Unreviewed' THEN ts END), "
			+ "COUNT(DISTINCT CASE WHEN ts.status IN ('Rejected' , 'Accepted' ,'Reviewing' ) THEN ts END), "
			+ "COUNT(t), a.title " + "FROM Assignment a " + "LEFT JOIN a.AssignmentQuestion t "
			+ "LEFT JOIN t.assignmentSubmissions ts "  
			+ "WHERE ( a.course.courseId =:courseId OR :courseId =0 )AND ( a.subject.subjectId =:subjectId  OR :subjectId =0) " + "AND a.isDeleted = 0 "
			+ "AND t.isDeleted = 0 " + "GROUP BY a.id, t.id, a.title, ts.status")
	List<Object[]> findAllAssignmentStatusWithCourseIdAndSubjectId(@Param("courseId") Integer courseId,
			@Param("subjectId") Integer subjectId);

	
	
	@Query("SELECT  "
			+ " NEW com.cico.payload.AssignmentSubmissionResponse(ts.student.applyForCourse ,ts.student.fullName ,ts.submissionDate ,ts.status,ts.student.profilePic,a.title,ts.submitFile,ts.description,ts.submissionId,ts.review) "
			+ "FROM Assignment a " + "JOIN a.AssignmentQuestion t "
			+ "  JOIN t.assignmentSubmissions ts "
			+ "WHERE ( a.course.courseId =:courseId OR :courseId =0 )AND ( a.subject.subjectId =:subjectId  OR :subjectId =0) AND (ts.status =:status OR :status ='NOT_CHECKED_WITH_IT' ) " + "AND a.isDeleted = 0 "
			+ "AND t.isDeleted = 0 " )
	List<AssignmentSubmissionResponse> findAllAssignmentSubmissionWithCourseIdAndSubjectId(@Param("courseId") Integer courseId,
			@Param("subjectId") Integer subjectId, SubmissionStatus status);

	@Query("SELECT " 
	         + "NEW com.cico.payload.TaskStatusSummary(  COUNT(ts) as totalCount, COUNT(CASE WHEN ts.status IN ('Rejected', 'Accepted', 'Reviewing') THEN ts END) as reviewedCount,COUNT(CASE WHEN ts.status = 'Unreviewed' THEN ts END) as unreviewedCount)"
			+ "FROM Assignment a " 
			+ " JOIN a.AssignmentQuestion t ON t.isDeleted = 0 "
			+ " JOIN t.assignmentSubmissions ts")
	TaskStatusSummary getOverAllAssignmentTaskStatus();
	
	@Query("SELECT  a.id , a.title ," 
	        + "COUNT(CASE WHEN ts.status = 'Unreviewed' THEN  ts  END), "
			+ "COUNT(CASE WHEN ts.status IN ('Rejected', 'Accepted', 'Reviewing') THEN ts END), "
			+ "COUNT(ts), "
			+ "COUNT (t),"
			+ " t.questionId "
			+ "FROM Assignment a " 
			+ "LEFT JOIN a.AssignmentQuestion t ON t.isDeleted = 0 "
			+ "LEFT JOIN t.assignmentSubmissions ts  "+
			"GROUP BY  a.id , ts , t "
			)
	List<Object[]> getAllSubmissionAssignmentTaskStatus();
}
