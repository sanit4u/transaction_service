package de.sanit4u.transaction.repo.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import de.sanit4u.transaction.exception.TransactionRepositoryException;
import de.sanit4u.transaction.model.Transaction;
import de.sanit4u.transaction.repo.ITransactionRepo;

/**
 * The repository stores the transaction in memory with the help of Maps.
 *
 */
@Component
public class TransactionRepoImpl implements ITransactionRepo<Transaction> {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	private static final Map<Long, Transaction> ID_TRANSACTION_MAP = new HashMap<Long, Transaction>();
	private static final Map<String, Set<Transaction>> TYPES_TRANSACTION_SECONDARY_MAP = new HashMap<String, Set<Transaction>>();
	private static final Map<Long, Set<Transaction>> PARENT_CHILD_TRANSACTION_SECONDARY_MAP = new HashMap<Long, Set<Transaction>>();

	/**
	 * Retrieves all the transactions available in memory
	 */
	@Override
	public List<Transaction> getAll() {
		log.debug("retrieve all the transactions.");

		return ID_TRANSACTION_MAP.values().stream().collect(Collectors.toList());
	}

	/**
	 * Stores the transaction
	 * 
	 * It builds the id 2 transaction index with
	 * {@link TransactionRepoImpl#buildTransactionIdIndex(Transaction)}
	 * 
	 * builds the type 2 transactions index with
	 * {@link #buildTransactionTypesIndex(Transaction, Transaction)}
	 * 
	 * 
	 * build the parent2child index with
	 * {@link #buildParentChildTransactionIndex(Transaction, Transaction)}
	 */
	@Override
	public Transaction save(Transaction newTransaction) {
		log.debug(String.format("recording Transaction for %d ", newTransaction.getId()));
		try {

			Transaction oldTransaction = ID_TRANSACTION_MAP.get(newTransaction.getId());

			buildTransactionIdIndex(newTransaction);

			buildTransactionTypesIndex(newTransaction, oldTransaction);

			buildParentChildTransactionIndex(newTransaction, oldTransaction);
		} catch (Exception e) {
			throw new TransactionRepositoryException(
					String.format("Transaction Repository Exception while saving the transaction for Id %d",
							newTransaction.getId()),
					e);
		}
		return ID_TRANSACTION_MAP.get(newTransaction.getId());
	}

	/**
	 * Retrieves the transaction through transaction id
	 */
	@Override
	public Optional<Transaction> findById(long id) {
		log.debug(String.format("retrieving Transaction by id %d ", id));

		return Optional.ofNullable(ID_TRANSACTION_MAP.get(id));
	}

	/**
	 * Retrieves all the transaction of same type
	 */
	@Override
	public List<Transaction> findByType(String type) {
		log.debug(String.format("retrieving Transaction by type %s ", type));

		return new ArrayList<Transaction>(TYPES_TRANSACTION_SECONDARY_MAP.get(type));
	}

	/**
	 * This retrieves all children including all nested children of all the levels
	 * of a given transaction id.
	 */
	@Override
	public List<Transaction> getAllNestedChildrenTransactions(long parentTransactionId) {
		log.debug(String.format("retrieving children transactions of transactionId %d ", parentTransactionId));
		List<Transaction> results = new ArrayList<Transaction>();
		try {

			Optional<Transaction> root = this.findById(parentTransactionId);

			if (!root.isPresent()) {
				return results;
			}

			Transaction rootTransaction = root.get();

			Set<Transaction> isVisited = new HashSet<Transaction>();

			Stack<Transaction> stack = new Stack<Transaction>();
			stack.push(rootTransaction);

			while (!stack.isEmpty() && rootTransaction != null) {
				Transaction currentTransaction = stack.pop();

				if (isVisited.contains(currentTransaction)) {
					continue;
				}

				results.add(currentTransaction);
				isVisited.add(currentTransaction);

				Set<Transaction> children = PARENT_CHILD_TRANSACTION_SECONDARY_MAP
						.getOrDefault(currentTransaction.getId(), new HashSet<Transaction>());

				children.stream().forEach(stack::push);
			}

		} catch (Exception e) {
			throw new TransactionRepositoryException(String.format(
					"Transaction Repository Exception while retriving all the children linked to the transaction Id %d",
					parentTransactionId), e);
		}
		return results;

	}

	private void buildTransactionIdIndex(Transaction t) {
		log.debug(String.format("building TransactionId Index for %d ", t.getId()));

		ID_TRANSACTION_MAP.put(t.getId(), t);
	}

	/**
	 * This clears all the data stored in memory.
	 */
	@Override
	public void clearAll() {
		ID_TRANSACTION_MAP.clear();
		PARENT_CHILD_TRANSACTION_SECONDARY_MAP.clear();
		TYPES_TRANSACTION_SECONDARY_MAP.clear();
	}

	private void buildTransactionTypesIndex(Transaction newTransaction, Transaction oldTransaction) {
		log.debug(String.format("building TransactionType Index for %d ", newTransaction.getId()));

		String oldType = oldTransaction == null ? null : oldTransaction.getType();

		refreshSecondaryMap(TYPES_TRANSACTION_SECONDARY_MAP, newTransaction, newTransaction.getType(), oldType);

	}

	private void buildParentChildTransactionIndex(Transaction newTransaction, Transaction oldTransaction) {
		log.debug(String.format("building parentChild Index for %d ", newTransaction.getId()));

		Long oldParentId = oldTransaction == null ? null : oldTransaction.getParent_id();

		refreshSecondaryMap(PARENT_CHILD_TRANSACTION_SECONDARY_MAP, newTransaction, newTransaction.getParent_id(),
				oldParentId);
	}

	private <T> void refreshSecondaryMap(Map<T, Set<Transaction>> map, Transaction transaction, T newKey, T oldKey) {

		removeOldEntry(map, transaction, oldKey);

		addNewEntry(map, transaction, newKey);
	}

	private <T> void addNewEntry(Map<T, Set<Transaction>> map, Transaction transaction, T newKey) {
		map.computeIfAbsent(newKey, parent -> new HashSet<Transaction>()).add(transaction);
	}

	private <T> void removeOldEntry(Map<T, Set<Transaction>> map, Transaction transaction, T oldKey) {

		if (oldKey == null) {
			return;
		}

		Set<Transaction> indexedTransactions = map.get(oldKey);
		indexedTransactions.remove(transaction);
	}

}
