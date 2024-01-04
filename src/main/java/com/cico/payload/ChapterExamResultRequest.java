package com.cico.payload;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class ChapterExamResultRequest {
	private Integer chapterId;
	private Integer studentId;
	private Integer subjectId;
	private Map<Integer,String>review = new HashMap<>();
	
}
