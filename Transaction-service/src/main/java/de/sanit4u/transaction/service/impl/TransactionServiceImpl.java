package de.sanit4u.transaction.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import de.sanit4u.transaction.dto.SumDTO;
import de.sanit4u.transaction.exception.TransactionServiceException;
import de.sanit4u.transaction.exception.TransactionNotFoundException;
import de.sanit4u.transaction.model.Transaction;
import de.sanit4u.transaction.repo.ITransactionRepo;
import de.sanit4u.transaction.service.TransactionService;

/**
 * This provides all the necessary operations for the controller
 *
 */
@Service
public class TransactionServiceImpl implements TransactionService {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	ITransactionRepo<Transaction> transactionRepo;

	/**
	 * This saves the Transaction . It uses {@code ITransactionRepo#save(Object)} to
	 * save the transaction
	 * 
	 */
	@Override
	public boolean recordTransaction(Transaction transaction) {
		log.debug(String.format("recording Transaction for %d ", transaction.getId()));

		Transaction savedTransaction = transactionRepo.save(transaction);
		if (savedTransaction == null) {
			throw new TransactionServiceException("Exception while recording the transaction");
		}

		log.debug(String.format("recorded Transaction for %d ", transaction.getId()));
		return true;
	}

	/**
	 * This retrieves all the id of the transaction that have same type.
	 */
	@Override
	public List<Long> retrieveTransactionByType(String type) {
		log.debug(String.format("retrieving Transaction by type %s ", type));

		List<Transaction> transactions = transactionRepo.findByType(type);

		log.debug(String.format("retrieved Transaction by %s ", type));

		return transactions.stream().map(Transaction::getId).collect(Collectors.toList());

	}

	/**
	 * This retrieve the transaction by transaction id
	 */
	@Override
	public Transaction retrieveTransaction(long transactionId) {
		log.debug(String.format("retrieving Transaction by id %d ", transactionId));

		// @formatter:off
		Transaction transaction = transactionRepo.findById(transactionId)
				.orElseThrow(() -> new TransactionNotFoundException(transactionId,
						String.format("Transaction having transaction id %d is not found", transactionId)));
		// @formatter:on

		log.debug(String.format("retrieved Transaction by %d ", transactionId));

		return transaction;
	}

	/**
	 * This retrieve the sum of all the amount belong to the linked (transitively)
	 * transaction (self inclusive) of a given transaction.
	 */
	@Override
	public SumDTO sumTransaction(long transactionId) {

		log.debug(String.format("retrieving the sum amount of all children of id %d ", transactionId));

		List<Transaction> allLinkedTransactions = transactionRepo.getAllNestedChildrenTransactions(transactionId);

		// @formatter:off
		double sum = allLinkedTransactions.stream()
				.mapToDouble(Transaction::getAmount)
				.sum();
		// @formatter:on

		SumDTO sumDto = new SumDTO(sum);

		log.debug(String.format("calculated the sum amount of all children of id %d ", transactionId));

		return sumDto;
	}

}
