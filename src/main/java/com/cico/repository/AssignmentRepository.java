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

	//List<Assignment> findByIsActiveTrue();
	List<Assignment> findByIsDeletedFalse();
   
	Course findByCourse(Optional<Course> findByCourseId);

	@Query("SELECT a FROM Assignment a WHERE a.course.courseId = :courseId")
     List<Assignment> findAllByCourseIdAndIsDeletedFalse(@Param("courseId") Integer courseId);
    
	@Query("SELECT a FROM Assignment a WHERE a.course.courseId = :courseId  AND a.subject.subjectId =:subjectId")
	List<Assignment> findAllByCourseIdAndSubjectIdAndIsDeletedFalse(@Param("courseId") Integer courseId,@Param("subjectId") Integer subjectId);

	@Query("SELECT a FROM Assignment a WHERE a.title =:title")
    Optional<Assignment> findByName(@Param("title") String title);

	
	
	
	
	
//	@Query("SELECT a FROM Assignment a JOIN FETCH a.AssignmentQuestion q WHERE a.isActive = true AND q.isActive = true")
//	Optional<Assignment> findByIdAndIsActive(Long id, boolean b);
//
//	@Query("SELECT a FROM Assignment a JOIN FETCH a.AssignmentQuestion q WHERE a.isActive = true AND q.isActive = true")
//	List<Assignment> findByIsActiveTrue();
//
//	Course findByCourse(Optional<Course> findByCourseId);
//
//	@Query("SELECT a FROM Assignment a JOIN FETCH a.AssignmentQuestion q WHERE a.isActive = true AND  a.course.courseId = :courseId  AND q.isActive = true")
//	// @Query("SELECT a FROM Assignment a WHERE a.course.courseId = :courseId")
//	List<Assignment> findAllByCourseIdAndIsActiveTrue(@Param("courseId") Integer courseId);
//
//	@Query("SELECT a FROM Assignment a JOIN FETCH a.AssignmentQuestion q WHERE a.isActive = true AND  a.course.courseId = :courseId  AND a.subject.subjectId =:subjectId AND q.isActive = true")
//	List<Assignment> findAllByCourseIdAndSubjectIdAndIsActiveTrue(@Param("courseId") Integer courseId,
//			@Param("subjectId") Integer subjectId);



}
