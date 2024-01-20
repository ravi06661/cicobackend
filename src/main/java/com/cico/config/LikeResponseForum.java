package com.cico.config;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LikeResponseForum {

	private String type;
	private Integer discussionFormId;
	private List<LikeResponse> likes = new ArrayList<>();
	private boolean isLike;
	private Integer studentId;
	private Integer likeId;
	public LocalDateTime createdDate;
	private String studentName;
	private String studentProfilePic;
	
	@Override
	public String toString() {
	    return "{"

	            + "\"type\":\"" + type + "\", "
	            + "\"discussionFormId\":" + discussionFormId + ", "
	            + "\"isLike\":" + isLike + ", "
	            + "\"studentId\":" + studentId + ", "
	            + "\"likeId\":" + likeId + ", "
	            + "\"createdDate\":\"" + createdDate + "\", "
	            + "\"studentName\":\"" + studentName + "\", "
	            + "\"studentProfilePic\":\"" + studentProfilePic + "\""
	            + "}";
	}
	
}
