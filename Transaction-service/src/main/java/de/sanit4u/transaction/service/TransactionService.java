package de.sanit4u.transaction.service;

import java.util.List;

import de.sanit4u.transaction.dto.SumDTO;
import de.sanit4u.transaction.model.Transaction;

public interface TransactionService {

	boolean recordTransaction(Transaction transaction);

	Transaction retrieveTransaction(long transactionId);

	List<Long> retrieveTransactionByType(String type);

	SumDTO sumTransaction(long transactionId);

}
