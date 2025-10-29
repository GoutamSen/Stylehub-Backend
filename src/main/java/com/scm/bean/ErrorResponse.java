package com.scm.bean;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class ErrorResponse {

	    private LocalDateTime timestamp;
	    private int status;
	    private String error;
	    private String message;
	    private String path;
}
