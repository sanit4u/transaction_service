package de.sanit4u.transaction.repo;

import java.util.List;

import de.sanit4u.transaction.model.Transaction;

public interface ITransactionRepo<T> extends IRepository<T>, IFindById<T>, IFindByType<T> {

	List<Transaction> getAllNestedChildrenTransactions(long parentTransactionId);

	void clearAll();

}
