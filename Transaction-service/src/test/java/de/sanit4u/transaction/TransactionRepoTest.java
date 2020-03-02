package de.sanit4u.transaction;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.junit4.SpringRunner;

import de.sanit4u.transaction.model.Transaction;
import de.sanit4u.transaction.repo.ITransactionRepo;
import de.sanit4u.transaction.repo.impl.TransactionRepoImpl;

/**
 * Mock test cases for testing the service only
 * 
 *
 */
@RunWith(SpringRunner.class)
public class TransactionRepoTest extends AbstractTransactionTest {

	@TestConfiguration
	static class TransactionRepoTestContextConfiguration {

		@Bean
		public ITransactionRepo<Transaction> transactionRepo() {
			return new TransactionRepoImpl();
		}
	}

	@Autowired
	private ITransactionRepo<Transaction> transactionRepo;

	@Test
	public void testSaveTransaction() {

		Transaction transaction = this.getDummyTransactionWithOutParent();

		testSave(transaction);
	}

	@Test
	public void testRetrieveTransactionById() {

		Transaction expectedTransaction = this.getDummyTransactionWithOutParent();

		testSave(expectedTransaction);

		Optional<Transaction> actualTransaction = transactionRepo.findById(expectedTransaction.getId());
		assertTrue(actualTransaction.isPresent());
		assertTrue(actualTransaction.get() == expectedTransaction);
	}

	@Test
	public void testRetrieveTransactionByType() {

		Transaction expectedTransaction = this.getDummyTransactionWithOutParent();

		testSave(expectedTransaction);

		List<Transaction> actualResults = transactionRepo.findByType(expectedTransaction.getType());

		assertNotNull(actualResults);
		assertTrue(actualResults.size() == 1);
		assertTrue(actualResults.get(0) == expectedTransaction);
	}

	@Test
	public void testSumTransactionOfParentId() {

		Transaction parentTransaction = this.getDummyTransactionWithOutParent();
		testSave(parentTransaction);

		Transaction childTransaction_level_1_A = this.getDummyTransactionWithParent(parentTransaction.getId());
		testSave(childTransaction_level_1_A);

		Transaction childTransaction_level_1_B = this.getDummyTransactionWithParent(parentTransaction.getId());
		testSave(childTransaction_level_1_B);

		Transaction childTransaction_level_2 = this.getDummyTransactionWithParent(childTransaction_level_1_A.getId());
		testSave(childTransaction_level_2);

		List<Transaction> expectedChildren_ROOT = Arrays.asList(parentTransaction, childTransaction_level_1_A,
				childTransaction_level_1_B, childTransaction_level_2);

		List<Transaction> allChildren = transactionRepo.getAllNestedChildrenTransactions(parentTransaction.getId());

		assertNotNull(allChildren);

		assertTrue(allChildren.size() == 4);
		assertTrue(allChildren.containsAll(expectedChildren_ROOT));

	}

	@Test
	public void testSumTransactionOfParentId_After_Update_same_Transaction() {

		Transaction parentTransaction = this.getDummyTransactionWithOutParent();
		testSave(parentTransaction);

		Transaction childTransaction_level_1_A = this.getDummyTransactionWithParent(parentTransaction.getId());
		testSave(childTransaction_level_1_A);

		Transaction childTransaction_level_1_B = this.getDummyTransactionWithParent(parentTransaction.getId());
		testSave(childTransaction_level_1_B);

		Transaction childTransaction_level_2 = this.getDummyTransactionWithParent(childTransaction_level_1_A.getId());
		testSave(childTransaction_level_2);

		List<Transaction> expectedChildren_ROOT = Arrays.asList(parentTransaction, childTransaction_level_1_A,
				childTransaction_level_1_B, childTransaction_level_2);

		List<Transaction> allChildren = transactionRepo.getAllNestedChildrenTransactions(parentTransaction.getId());

		assertNotNull(allChildren);

		assertTrue(allChildren.size() == 4);
		assertTrue(allChildren.containsAll(expectedChildren_ROOT));

		// new transaction with same id and different ParentId
		Transaction childTransaction_level_2_Edit = this
				.getDummyTransactionWithParent(childTransaction_level_1_B.getId());

		childTransaction_level_2_Edit.setId(childTransaction_level_2.getId());
		childTransaction_level_2_Edit.setAmount(childTransaction_level_2.getAmount());
		childTransaction_level_2_Edit.setType(childTransaction_level_2.getType());

		testSave(childTransaction_level_2_Edit);

		List<Transaction> allChildren_afterEdit = transactionRepo
				.getAllNestedChildrenTransactions(parentTransaction.getId());

		assertNotNull(allChildren_afterEdit);

		assertTrue(allChildren_afterEdit.size() == 4);
		assertTrue(allChildren_afterEdit.containsAll(expectedChildren_ROOT));

		List<Transaction> children_Level_1_B_afterEdit = transactionRepo
				.getAllNestedChildrenTransactions(childTransaction_level_1_B.getId());

		assertNotNull(children_Level_1_B_afterEdit);

		assertTrue(children_Level_1_B_afterEdit.size() == 2);
		assertTrue(children_Level_1_B_afterEdit.contains(childTransaction_level_2_Edit));
	}

	@After
	public void tearDown() {

		transactionRepo.clearAll();
	}

	private void testSave(Transaction transaction) {
		Transaction actualTransaction = transactionRepo.save(transaction);

		assertNotNull(actualTransaction);
		assertTrue(actualTransaction == transaction);
	}

}
