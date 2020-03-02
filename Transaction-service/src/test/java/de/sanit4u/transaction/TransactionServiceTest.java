package de.sanit4u.transaction;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.junit4.SpringRunner;

import de.sanit4u.transaction.dto.SumDTO;
import de.sanit4u.transaction.exception.TransactionException;
import de.sanit4u.transaction.exception.TransactionNotFoundException;
import de.sanit4u.transaction.model.Transaction;
import de.sanit4u.transaction.repo.ITransactionRepo;
import de.sanit4u.transaction.service.TransactionService;
import de.sanit4u.transaction.service.impl.TransactionServiceImpl;

/**
 * Mock test cases for testing the service only
 * 
 *
 */
@RunWith(SpringRunner.class)
public class TransactionServiceTest extends AbstractTransactionTest {

	@TestConfiguration
	static class TransactionServiceImplTestContextConfiguration {

		@Bean
		@Primary
		public TransactionService transactionService() {
			return new TransactionServiceImpl();
		}
	}

	@Autowired
	private TransactionService transactionService;

	@MockBean
	private ITransactionRepo<Transaction> transactionRepo;

	@Test
	public void testRecordTransaction_SUCCESS() {

		Transaction transaction = this.getDummyTransactionWithOutParent();

		Mockito.when(transactionRepo.save(transaction)).thenReturn(transaction);

		boolean isSuccess = transactionService.recordTransaction(transaction);

		assertTrue(isSuccess);
	}

	@Test(expected = TransactionException.class)
	public void testRecordTransaction_EXCEPTION() {
		Transaction transaction = this.getDummyTransactionWithOutParent();

		Mockito.when(transactionRepo.save(transaction)).thenReturn(null);

		transactionService.recordTransaction(transaction);
	}

	@Test
	public void testRetrieveTransactionById() {

		Transaction expectedTransaction = this.getDummyTransactionWithOutParent();

		Mockito.when(transactionRepo.findById(expectedTransaction.getId()))
				.thenReturn(Optional.ofNullable(expectedTransaction));

		Transaction actualTransaction = transactionService.retrieveTransaction(expectedTransaction.getId());

		verify(transactionRepo, times(1)).findById(Mockito.anyLong());

		assertNotNull(actualTransaction);
		assertTrue(actualTransaction.getId() == expectedTransaction.getId()
				&& actualTransaction.getType() == expectedTransaction.getType());
	}

	@Test(expected = TransactionNotFoundException.class)
	public void testRetrieveTransactionById_EXCEPTION() {
		Transaction transaction = this.getDummyTransactionWithOutParent();

		Mockito.when(transactionRepo.findById(transaction.getId())).thenReturn(Optional.empty());

		transactionService.retrieveTransaction(transaction.getId());
	}

	@Test
	public void testRetrieveTransactionByType() {

		Transaction expectedTransaction = this.getDummyTransactionWithOutParent();
		List<Transaction> expectedResult = Arrays.asList(expectedTransaction);

		Mockito.when(transactionRepo.findByType(expectedTransaction.getType())).thenReturn(expectedResult);

		List<Long> actualResult = transactionService.retrieveTransactionByType(expectedTransaction.getType());

		verify(transactionRepo, times(1)).findByType(Mockito.anyString());

		assertNotNull(actualResult);
		assertTrue(actualResult.size() == 1 && actualResult.get(0) == expectedTransaction.getId());
	}

	@Test
	public void testSumTransactionOfParentId() {

		Transaction parentTransaction = this.getDummyTransactionWithOutParent();
		Transaction childTransaction_level_1_A = this.getDummyTransactionWithParent(parentTransaction.getId());
		Transaction childTransaction_level_1_B = this.getDummyTransactionWithParent(parentTransaction.getId());
		Transaction childTransaction_level_2 = this.getDummyTransactionWithParent(childTransaction_level_1_A.getId());

		// @formatter:off
		double expectedResult = parentTransaction.getAmount() 
				+ childTransaction_level_1_A.getAmount() + childTransaction_level_1_B.getAmount() 
				+ childTransaction_level_2.getAmount();
		// @formatter:on

		List<Transaction> children = Arrays.asList(parentTransaction, childTransaction_level_1_A,
				childTransaction_level_1_B, childTransaction_level_2);

		Mockito.when(transactionRepo.getAllNestedChildrenTransactions(parentTransaction.getId())).thenReturn(children);

		SumDTO actualResult = transactionService.sumTransaction(parentTransaction.getId());

		verify(transactionRepo, times(1)).getAllNestedChildrenTransactions(Mockito.anyLong());

		assertNotNull(actualResult);
		assertTrue(actualResult.getSum() == expectedResult);

	}

}
