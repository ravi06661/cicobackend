package com.cico.payload;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class CommentResponse {

	private Integer id;
	private LocalDateTime createdDate;
	private String content;
	private String studentName;
	private String studentProfilePic;
	private Integer studentId;
	private String file;
    private List<CommentReplyResponse>commentReplyResponses;

}
