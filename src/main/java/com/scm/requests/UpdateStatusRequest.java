package com.scm.requests;

import com.scm.entities.Status;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class UpdateStatusRequest {

	private String email;
	private Status newStatus;
}
