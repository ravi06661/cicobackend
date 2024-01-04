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
}
