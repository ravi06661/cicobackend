package com.cico.payload;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class SubjectResponse {

	private Integer subjectId;
	private String subjectName;
	private Boolean isDeleted;
	private Boolean isActive;
	public Long chapterCount;
	public Long chapterCompleted;
	private String technologyName;
	private TechnologyStackResponse technologyStack;
	

}
