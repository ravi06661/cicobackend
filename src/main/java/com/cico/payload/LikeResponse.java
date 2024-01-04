package com.cico.payload;

import lombok.Data;

@Data
public class LikeResponse {

	private Integer id;
	//private LocalDateTime createdDate;
	private String studentName;
	private String studentProfilePic;
	private Integer studentId;

}
