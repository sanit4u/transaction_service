package de.sanit4u.transaction.exception;

public class TransactionException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionException(String message) {
		super(message);
	}

	public TransactionException(String message, Throwable t) {
		super(message, t);
	}

}
