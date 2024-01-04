package com.cico.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cico.model.Subject;
import com.cico.model.Task;

public interface TaskRepo extends JpaRepository<Task, Long> {

	Task findByTaskNameAndIsDeleted(String taskName, boolean b);

	Object findByTaskName(String taskName);

	Optional<Task> findByTaskIdAndIsDeleted(Long taskId, boolean b);
  
	
	List<Task> findByIsDeletedFalse();

    List<Task> findBySubjectAndIsDeletedFalse(Subject subject);
     
//    @Modifying
//    @Transactional
//    @Query("UPDATE Task t SET t.taskQuestion = :newTaskQuestionList WHERE :taskQuestion NOT MEMBER OF t.taskQuestion AND t.taskId = :taskId")
//    void deleteTaskQuestionByTaskId(@Param("taskId") int taskId, @Param("taskQuestion") AssignmentTaskQuestion assignmentTaskQuestion, @Param("newTaskQuestionList") List<AssignmentTaskQuestion> newTaskQuestionList);


}
