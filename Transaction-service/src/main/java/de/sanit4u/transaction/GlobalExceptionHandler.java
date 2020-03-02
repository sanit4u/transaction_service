package de.sanit4u.transaction;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import de.sanit4u.transaction.controller.response.RestResponse;
import de.sanit4u.transaction.exception.TransactionException;
import de.sanit4u.transaction.exception.TransactionNotFoundException;

/**
 * Global exception Handler
 *
 */
@ControllerAdvice
public class GlobalExceptionHandler {

	@ExceptionHandler({ TransactionException.class })
	public ResponseEntity<?> internalServerException(TransactionException ex) {
		RestResponse response = new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage());

		return new ResponseEntity<RestResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(TransactionNotFoundException.class)
	public ResponseEntity<RestResponse> requestNotFound(TransactionNotFoundException ex) {
		RestResponse response = new RestResponse(HttpStatus.NOT_FOUND.name(), ex.getMessage());

		return new ResponseEntity<RestResponse>(response, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<RestResponse> badRequestFound(MethodArgumentNotValidException ex) {
		RestResponse response = new RestResponse(HttpStatus.BAD_REQUEST.name(), ex.getLocalizedMessage());

		return new ResponseEntity<RestResponse>(response, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<RestResponse> general(Exception ex) {
		RestResponse response = new RestResponse(HttpStatus.INTERNAL_SERVER_ERROR.name(), ex.getMessage());

		return new ResponseEntity<RestResponse>(response, HttpStatus.INTERNAL_SERVER_ERROR);
	}

}
