package com.scm.exception;

public class UserBlockedException extends RuntimeException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public UserBlockedException() {
		super();
	}

	public UserBlockedException(String message) {
		super(message);
	}
}
