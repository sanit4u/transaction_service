package de.sanit4u.transaction.exception;

public class TransactionNotFoundException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@SuppressWarnings("unused")
	private long transactionId;

	public TransactionNotFoundException(long transactionId, String message) {
		super(message);
		this.transactionId = transactionId;
	}

	/**
	 * @return the transactionId
	 */
	public long getTransactionId() {
		return transactionId;
	}

}