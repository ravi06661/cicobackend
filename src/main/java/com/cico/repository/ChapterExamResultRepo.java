package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.cico.model.Chapter;
import com.cico.model.ChapterExamResult;
import com.cico.model.Student;

public interface ChapterExamResultRepo extends JpaRepository<ChapterExamResult, Integer>{

	public Optional<ChapterExamResult> findByChapterAndStudent(Chapter chapter, Student student);
	
	
   @Query("SELECT c	 FROM ChapterExamResult c WHERE c.chapter.chapterId =:id")
	public List<ChapterExamResult> findAllById (@Param("id")Integer id);

	
}
