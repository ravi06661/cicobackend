package com.cico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cico.model.Chapter;

public interface ChapterRepository extends JpaRepository<Chapter, Integer> {

//	@Query("SELECT c FROM Chapter c LEFT JOIN  FETCH c.chapterContent as co WHERE c.chapterId = :chapterId AND c.isDeleted = :isDeleted  AND( co.isDeleted IS NULL OR co.isDeleted = :isDeleted)")
//	Optional<Chapter> findByChapterIdAndIsDeleted(@Param("chapterId") Integer chapterId,@Param("isDeleted") Boolean isDeleted);
//
//	@Query("SELECT c FROM Chapter  c LEFT JOIN  FETCH c.chapterContent  as co WHERE c.chapterName = :chapterName AND c.isDeleted = :isDeleted  AND (co.isDeleted IS NULL  OR co.isDeleted = :isDeleted)")
//	Chapter findByChapterNameAndIsDeleted(@Param("chapterName") String chapterName,@Param("isDeleted") boolean isDeleted);
	
	@Query("SELECT c FROM Chapter c  WHERE c.chapterId = :chapterId AND c.isDeleted = :isDeleted ")
	Optional<Chapter> findByChapterIdAndIsDeleted(@Param("chapterId") Integer chapterId,@Param("isDeleted") Boolean isDeleted);

	@Query("SELECT c FROM Chapter  c WHERE c.chapterName = :chapterName AND c.isDeleted = :isDeleted  ")
	Chapter findByChapterNameAndIsDeleted(@Param("chapterName") String chapterName,@Param("isDeleted") boolean isDeleted);
}
