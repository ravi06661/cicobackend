package com.cico.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long taskId;

	private String taskName;

	private String taskAttachment;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<TaskQuestion> TaskQuestion = new ArrayList<>();;

	@OneToOne
	private Course course;

	@OneToOne
	private Subject subject;

	private String attachmentStatus;

	private Boolean isDeleted;
}
