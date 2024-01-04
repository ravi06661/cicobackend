package com.cico.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cico.model.DiscusssionForm;
import com.cico.model.Student;


@Repository
public interface DiscussionFormRepo extends JpaRepository<DiscusssionForm, Integer> {
   
  // @Query("SELECT a FROM  DiscusssionForm a ORDER BY createdDate DESC")
	//List<DiscusssionForm>findAll();

}
