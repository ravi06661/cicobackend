package com.cico.payload;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonInclude(value = Include.NON_NULL)
public class AssignmentTaskFilterReponse {

	private Long questionId;
	private String question;
	private String videoUrl;
	private List<String> questionImages;
     
	@SuppressWarnings("unchecked")
	public AssignmentTaskFilterReponse(Long questionId, String question, String videoUrl, Object questionImages) {
		super();
		this.questionId = questionId;
		this.question = question;
		this.videoUrl = videoUrl;
		System.out.println(questionImages);
		this.questionImages = (List<String>)questionImages;
		
		
	}

}
