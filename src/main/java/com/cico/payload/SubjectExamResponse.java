package com.cico.payload;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;

@Data
@JsonInclude(value = Include.NON_NULL)
public class SubjectExamResponse {

	private String subjectName;
	private Integer examId;
	private Integer subjectId;
	private String examImage;
	private boolean isActive;
	private Integer examTimer;
	private Long totalQuestion;
	private Integer passingMarks;
	private Integer scoreGet;
	private LocalDateTime startTimer;

	public SubjectExamResponse(String subjectName, Integer examId, Integer subjectId, String examImage,
			boolean isActive, Integer examTimer, Long totalQuestion, Integer passingMarks, Integer scoreGet,
			LocalDateTime startTimer) {
		super();
		this.subjectName = subjectName;
		this.examId = examId;
		this.subjectId = subjectId;
		this.examImage = examImage;
		this.isActive = isActive;
		this.examTimer = examTimer;
		this.totalQuestion = totalQuestion;
		this.passingMarks = passingMarks;
		this.scoreGet = scoreGet;
		this.startTimer = startTimer;
	}

}
