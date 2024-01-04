package com.cico.repository;

import java.util.List;
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
	Optional<Chapter> findByChapterIdAndIsDeleted(@Param("chapterId") Integer chapterId,
			@Param("isDeleted") Boolean isDeleted);

	@Query("SELECT c FROM Chapter  c WHERE c.chapterName = :chapterName AND c.isDeleted = :isDeleted  ")
	Chapter findByChapterNameAndIsDeleted(@Param("chapterName") String chapterName,
			@Param("isDeleted") boolean isDeleted);

	@Query("SELECT  c.chapterName , cc.id,cc.title,cc.subTitle,cc.content FROM  Chapter c LEFT JOIN  c.chapterContent cc ON cc.isDeleted =0   WHERE  c.chapterId =:chapterId ")
	List<Object[]> getChapterContentWithChapterId(Integer chapterId);

//	private Integer questionId;
//	private String questionContent;
//	private String option1;
//	private String option2;
//	private String option3;
//	private String option4;
//	private String correctOption;
//	private String selectedOption;// select by student 
//	private String questionImage;
//	
  
//	@Query("SELECT cq.questionId , cq.questionContent ,  cq.option1 , cq.option2 ,  cq.option3  cq.option4  , cq.correctOption ,  cq.selectedOption , cq.questionImage  ")
//	List<Object[]> getChaperExamQuestions(Integer chapterId);

}
