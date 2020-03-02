package de.sanit4u.transaction.exception;

public class TransactionRepositoryException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public TransactionRepositoryException(String message, Throwable t) {
		super(message, t);
	}

}