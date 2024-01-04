package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cico.model.Subject;

public interface SubjectRepository extends JpaRepository<Subject, Integer> {

//	@Query("SELECT s FROM Subject s LEFT JOIN FETCH s.chapters  as c WHERE s.subjectName = :subjectName AND s.isDeleted = 0 AND  ( c.isDeleted IS NULL OR c.isDeleted = 0)")
//	Subject findBySubjectNameAndIsDeleted(@Param("subjectName") String subjectName);
//
//	@Query("SELECT s FROM Subject s LEFT JOIN FETCH s.chapters as  c WHERE s.subjectId = :subjectId AND  ( c.isDeleted IS NULL OR c.isDeleted = 0) ")
//	Optional<Subject> findBySubjectIdAndIsDeleted(@Param("subjectId") Integer subjectId);
//
//	@Query("SELECT  s FROM Subject s LEFT JOIN FETCH s.chapters as c WHERE  s.isDeleted = 0 AND ( c.isDeleted IS NULL OR c.isDeleted = 0) ")
//	List<Subject> findByIsDeletedFalse();
	
	@Query("SELECT s FROM Subject s  WHERE s.subjectName = :subjectName AND s.isDeleted = 0 ")
	Subject findBySubjectNameAndIsDeleted(@Param("subjectName") String subjectName);

	@Query("SELECT s FROM Subject s  WHERE s.subjectId = :subjectId AND s.isDeleted=0  ")
	Optional<Subject> findBySubjectIdAndIsDeleted(@Param("subjectId") Integer subjectId);

	
	@Query("SELECT s.subjectId,s.subjectName, s.technologyStack.technologyName, s.technologyStack.imageName, COUNT( DISTINCT c) ,s.technologyStack.id " +
	        "FROM Subject s " +
	        "LEFT JOIN s.chapters c ON c.isDeleted = 0 " +
	        "WHERE s.isDeleted = 0 " +
	        "GROUP BY s.subjectId")
	List<Object[]> findByIsDeletedFalse();
	
	@Query("SELECT  s.subjectId ,s.technologyStack.imageName ,s.subjectName,c.chapterId  , c.chapterName FROM Subject s   LEFT JOIN s.chapters  c ON c.isDeleted = 0      WHERE  s.isDeleted =0  AND  s.subjectId =:subjectId ")
	List<Object[]> getAllChapterWithSubjectId(Integer subjectId);

}
