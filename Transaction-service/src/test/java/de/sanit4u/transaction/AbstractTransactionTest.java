package de.sanit4u.transaction;

import java.security.SecureRandom;

import de.sanit4u.transaction.model.Transaction;

public class AbstractTransactionTest {

	private static SecureRandom random;

	public AbstractTransactionTest() {
		random = new SecureRandom();
	}

	protected Transaction getDummyTransactionWithOutParent() {

		Transaction t = new Transaction();
		t.setId(random.nextLong());
		t.setAmount(1000.0);
		t.setType("cars");

		return t;
	}

	protected Transaction getDummyTransactionWithParent(long parentId) {

		Transaction t = new Transaction();
		t.setId(random.nextLong());
		t.setAmount(1000.0);
		t.setType("cars");
		t.setParent_id(parentId);

		return t;
	}

}
