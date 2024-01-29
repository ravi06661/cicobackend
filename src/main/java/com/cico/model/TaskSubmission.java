package com.cico.model;

import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

import com.cico.util.SubmissionStatus;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class TaskSubmission {
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String taskDescription;
	private String submittionFileName;
	@OneToOne
	private Student student;

	//private Long taskId;
	private LocalDateTime submissionDate;
	@Enumerated(EnumType.STRING)
	private SubmissionStatus status;
	private String review;
	private String taskName;
}
