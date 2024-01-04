package com.cico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.cico.model.AssignmentTaskQuestion;

@Repository
public interface AssignmentTaskQuestionRepository extends JpaRepository<AssignmentTaskQuestion, Integer> {

	Optional<AssignmentTaskQuestion> findByQuestionId(Long questionId);

	@Modifying
    @Transactional
	@Query("UPDATE AssignmentTaskQuestion a set a.isDeleted = 0 WHERE a.questionId =:questionId")
	void deleteQuestionByIdAndId(Long questionId);

	// List<AssignmentTaskQuestion> findByAssignmentIdAndIsDeletedFalse(Long id);
}
