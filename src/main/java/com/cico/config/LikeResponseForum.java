package com.cico.config;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponseForum {

	private String type;
	private Integer discussionFormId;
	private List<LikeResponse> likes = new ArrayList<>();
	private boolean isLike;
	private Integer studentId;
}
