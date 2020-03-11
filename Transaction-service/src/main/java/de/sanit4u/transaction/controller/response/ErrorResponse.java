package de.sanit4u.transaction.controller.response;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;

public class ErrorResponse extends RestResponse {
	private List<String> errors;

	public ErrorResponse(HttpStatus status, String message, List<String> errors) {
		super(status, message);
		this.errors = errors;
	}

	public ErrorResponse(HttpStatus status, String message, String error) {
		super(status, message);
		errors = Arrays.asList(error);
	}

	/**
	 * @return the status
	 */
	public HttpStatus getStatus() {
		return status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @return the errors
	 */
	public List<String> getErrors() {
		return errors;
	}

}
