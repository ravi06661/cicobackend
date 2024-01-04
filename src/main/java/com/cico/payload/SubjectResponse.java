package com.cico.payload;

import java.util.List;

import com.cico.model.Chapter;
import com.cico.model.TechnologyStack;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubjectResponse {

	private Integer subjectId;
	private String subjectName;
	private List<Chapter> chapters;
	private TechnologyStack technologyStack;
	private Boolean isDeleted;
	private Boolean isActive;
	public Integer chapterCount;
	public Long chapterCompleted;
}
