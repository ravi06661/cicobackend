package com.cico.model;

import java.time.LocalDate;
import java.time.LocalTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Batch {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Integer batchId;
	private String batchName;
	private LocalDate batchStartDate;
	private LocalTime batchTiming;
	
	@Column(columnDefinition = "longtext")
	private String batchDetails;
	
//	@ManyToOne
//	private TechnologyStack technologyStack;
	
	@ManyToOne
	private Subject subject;
	
	
	private boolean isDeleted=false;
	private boolean isActive=true;
	public Batch(String batchName, LocalDate batchStartDate, LocalTime batchTiming,String batchDetails) {
		super();
		this.batchName = batchName;
		this.batchStartDate = batchStartDate;
		this.batchTiming = batchTiming;
		this.batchDetails = batchDetails;
	}

}
