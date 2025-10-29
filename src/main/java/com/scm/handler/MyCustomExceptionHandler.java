package com.scm.handler;

import java.time.LocalDateTime;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import com.scm.bean.ErrorResponse;
import com.scm.exception.ResourceNotFoundException;
import com.scm.exception.UserAlreadyExistsException;
import com.scm.exception.UserBlockedException;
import com.scm.exception.UserNotFoundException;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class MyCustomExceptionHandler {

	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(UserAlreadyExistsException exception,
			HttpServletRequest request) {
		ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.error(HttpStatus.CONFLICT.getReasonPhrase()) // "Conflict"
				.status(HttpStatus.CONFLICT.value()).message(exception.getMessage()).path(request.getRequestURI())
				.build();

		return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFoundException(UserNotFoundException exception,
			HttpServletRequest request) {
		ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase()) // "Conflict"
				.status(HttpStatus.NOT_FOUND.value()).message(exception.getMessage()).path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> resourceNotFoundException(ResourceNotFoundException exception,
			HttpServletRequest request) {
		ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.error(HttpStatus.NOT_FOUND.getReasonPhrase()) // "Conflict"
				.status(HttpStatus.NOT_FOUND.value()).message(exception.getMessage()).path(request.getRequestURI())
				.build();
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
	}

	@ExceptionHandler(UserBlockedException.class)
	public ResponseEntity<ErrorResponse> handleUserBlocked(UserBlockedException ex, HttpServletRequest request) {
		ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.error(HttpStatus.FORBIDDEN.getReasonPhrase()) // "Conflict"
				.status(HttpStatus.FORBIDDEN.value()).message(ex.getMessage()).path(request.getRequestURI()).build();
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);

	}

	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> handleRuntime(RuntimeException ex,HttpServletRequest request) {
		ErrorResponse response = ErrorResponse.builder().timestamp(LocalDateTime.now())
				.error(HttpStatus.UNAUTHORIZED.getReasonPhrase()) // "Conflict"
				.status(HttpStatus.UNAUTHORIZED.value()).message(ex.getMessage()).path(request.getRequestURI()).build();
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
	}

}
