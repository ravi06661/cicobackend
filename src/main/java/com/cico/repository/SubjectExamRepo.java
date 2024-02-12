package com.cico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.cico.model.SubjectExam;

@Repository
public interface SubjectExamRepo extends JpaRepository<SubjectExam, Integer> {

}
