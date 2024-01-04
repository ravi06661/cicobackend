package com.cico.repository;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cico.model.DiscussionFormComment;

@Repository
public interface DiscussionFormCommentRepo extends JpaRepository<DiscussionFormComment, Integer> {

	

}
