package com.cico.payload;

import java.time.LocalDate;

import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

import com.cico.model.Course;
import com.cico.model.Student;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FeesResponse {

	private Integer feesId;
	
	private Student student;

	private Course course;
	private Double finalFees;
	private Double feesPaid;
	private Double remainingFees;
	private LocalDate date;
	private LocalDate createdDate;
	private LocalDate updatedDate;
	private Boolean isCompleted=false;
}
