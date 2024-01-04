package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cico.model.Assignment;
import com.cico.model.Course;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

	Optional<Assignment> findByIdAndIsDeleted(Long id, boolean b);

	// List<Assignment> findByIsActiveTrue();
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
			+ "LEFT JOIN t.assignmentSubmissions ts " + "WHERE a.course.courseId =:courseId " + "AND a.isDeleted = 0 "
			+ "AND t.isDeleted = 0 " + "GROUP BY a.id, t.id, a.title, ts.status")
	List<Object[]> findAllAssignmentStatusWithCourseId(@Param("courseId") Integer courseId);

	@Query("SELECT  t.id, " + "COUNT(DISTINCT ts), "
			+ "COUNT(DISTINCT CASE WHEN ts.status = 'Unreviewed' THEN ts END), "
			+ "COUNT(DISTINCT CASE WHEN ts.status IN ('Rejected' , 'Accepted' ,'Reviewing' ) THEN ts END), "
			+ "COUNT(t), a.title " + "FROM Assignment a " + "LEFT JOIN a.AssignmentQuestion t "
			+ "LEFT JOIN t.assignmentSubmissions ts "
			+ "WHERE a.course.courseId =:courseId  AND  a.subject.subjectId =:subjectId " + "AND a.isDeleted = 0 "
			+ "AND t.isDeleted = 0 " + "GROUP BY a.id, t.id, a.title, ts.status")
	List<Object[]> findAllAssignmentStatusWithCourseIdAndSubjectId(@Param("courseId") Integer courseId,
			@Param("subjectId") Integer subjectId);

	@Query("SELECT " 
	         + "COUNT(CASE WHEN ts.status = 'Unreviewed' THEN ts  END), "
			+ "COUNT(CASE WHEN ts.status IN ('Rejected', 'Accepted', 'Reviewing') THEN ts END), "
			+ "COUNT(ts) " 
			+ "FROM Assignment a " 
			+ "LEFT JOIN a.AssignmentQuestion t ON t.isDeleted = 0 "
			+ "LEFT JOIN t.assignmentSubmissions ts ")
	Object[] getOverAllAssignmentTaskStatus();
    
	
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
