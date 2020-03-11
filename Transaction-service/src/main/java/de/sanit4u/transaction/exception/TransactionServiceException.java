package de.sanit4u.transaction.exception;

public class TransactionServiceException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionServiceException(String message) {
		super(message);
	}

	public TransactionServiceException(String message, Throwable t) {
		super(message, t);
	}

}
