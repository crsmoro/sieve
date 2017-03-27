package com.shuffle.sieve.core.exception;

public class SieveException extends RuntimeException {

	private static final long serialVersionUID = -2723995691408159451L;

	private String message;

	public SieveException(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
