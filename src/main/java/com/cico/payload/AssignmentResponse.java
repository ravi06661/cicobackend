package com.cico.payload;

import java.util.ArrayList;
import java.util.List;

import com.cico.model.Assignment;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssignmentResponse {
   
	List<Assignment>assignments = new ArrayList<>();
	private Boolean isCompleted;
}
