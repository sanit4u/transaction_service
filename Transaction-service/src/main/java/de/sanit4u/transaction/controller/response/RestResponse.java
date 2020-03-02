package de.sanit4u.transaction.controller.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * Rest Response
 *
 */
public class RestResponse {
	private String status;

	@JsonInclude(Include.NON_NULL)
	private String message;

	public RestResponse(String status) {
		this.status = status;
	}

	public RestResponse(String status, String message) {
		this.status = status;
		this.message = message;
	}

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

}