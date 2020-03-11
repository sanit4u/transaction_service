package de.sanit4u.transaction.controller.response;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Rest Response
 *
 */
public class RestResponse {
	protected final HttpStatus status;

	@JsonInclude(Include.NON_NULL)
	protected String message;

	public RestResponse(HttpStatus status) {
		this.status = status;
	}

	public RestResponse(HttpStatus status, String message) {
		this.status = status;
		this.message = message;
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

}