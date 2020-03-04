package de.sanit4u.transaction;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import de.sanit4u.transaction.model.Transaction;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class TransactionControllerMockMVCTest extends AbstractTransactionTest {
	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private MockMvc mvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Before
	public void before() {

		log.debug("--------------------------------");
	}

	@Test
	public void testRecordTransactionWithMockMVC() throws Exception {

		log.debug("Test execution : testRecordTransactionWithMockMVC ");
		Transaction transaction = this.getDummyTransactionWithOutParent();
		long id = transaction.getId();

		String request = objectMapper.writeValueAsString(transaction);

		//@formatter:off
		 mvc.perform(put("/transactionservice/transaction/{transactionId}", id)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(request))
			      .andExpect(status().isOk())
			      .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(jsonPath("$.status", is("OK")));
		 //@formatter:on
	}

	@Test
	public void testRecordTransactionWithMockMVC_BAD_Request() throws Exception {
		log.debug("Test execution : testRecordTransactionWithMockMVC_BAD_Request ");

		Transaction transaction = this.getDummyTransactionWithOutParent();
		long id = transaction.getId();
		transaction.setType(null);

		String request = objectMapper.writeValueAsString(transaction);

		//@formatter:off
		mvc.perform(put("/transactionservice/transaction/{transactionId}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
		.andExpect(status().isBadRequest());
		//@formatter:on
	}

	@Test
	public void testRetrieveTransactionByIDWithMockMVC() throws Exception {
		log.debug("Test execution : testRetrieveTransactionByIDWithMockMVC ");

		Transaction transaction = this.getDummyTransactionWithOutParent();
		long id = transaction.getId();

		String request = objectMapper.writeValueAsString(transaction);

		//@formatter:off
		 mvc.perform(put("/transactionservice/transaction/{transactionId}", id)
			      .contentType(MediaType.APPLICATION_JSON)
			      .content(request))
			      .andExpect(status().isOk())
			      .andExpect(content()
			      .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
			      .andExpect(jsonPath("$.status", is("OK")));
		 //@formatter:on

		//@formatter:off
		mvc.perform(get("/transactionservice/transaction/{transactionId}", id))
		.andExpect(status().isOk())
		.andExpect(content()
				.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.amount", is(transaction.getAmount())))
		.andExpect(jsonPath("$.type", is(transaction.getType())))
		.andExpect(jsonPath("$.parent_id", is(nullValue())));
		//@formatter:on

	}

	@Test
	public void testRetrieveTransactionByIDWithMockMVC_NOT_FOUND() throws Exception {
		log.debug("Test execution : testRetrieveTransactionByIDWithMockMVC_NOT_FOUND ");
		Transaction transaction = this.getDummyTransactionWithOutParent();
		long id = transaction.getId();

		//@formatter:off
		mvc.perform(get("/transactionservice/transaction/{transactionId}", id))
		.andExpect(status().isNotFound());
		//@formatter:on

	}

	@Test
	public void testRetrieveTransactionByTypeWithMockMVC() throws Exception {
		log.debug("Test execution : testRetrieveTransactionByTypeWithMockMVC ");

		Transaction transaction = this.getDummyTransactionWithOutParent();
		Long id = transaction.getId();
		String type = transaction.getType();

		testSave(transaction, id);

		//@formatter:off
		mvc.perform(get("/transactionservice/types/{type}", type))
		.andExpect(status().isOk())
		.andExpect(content()
				.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$").isArray())
		.andExpect(jsonPath("$", hasItem(id)));
		//@formatter:on
	}

	@Test
	public void testRetrieveSumOfAllChildrenTransactionWithMockMVC() throws Exception {
		log.debug("Test execution : testRetrieveSumOfAllChildrenTransactionWithMockMVC ");

		Transaction parent = this.getDummyTransactionWithOutParent();
		Long id = parent.getId();
		testSave(parent, id);

		Transaction child_level_1 = this.getDummyTransactionWithParent(parent.getId());
		testSave(child_level_1, child_level_1.getId());

		Transaction child_level_2 = this.getDummyTransactionWithParent(child_level_1.getId());
		testSave(child_level_2, child_level_2.getId());

		double expectedSumResult = parent.getAmount() + child_level_1.getAmount() + child_level_2.getAmount();

		//@formatter:off
		mvc.perform(get("/transactionservice/sum/{transactionId}", id))
		.andExpect(status().isOk())
		.andExpect(content()
				.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.sum", is(expectedSumResult)));
		//@formatter:on
	}

	private void testSave(Transaction parent, Long id) throws JsonProcessingException, Exception {
		String request = objectMapper.writeValueAsString(parent);

		//@formatter:off
		mvc.perform(put("/transactionservice/transaction/{transactionId}", id)
				.contentType(MediaType.APPLICATION_JSON)
				.content(request))
		.andExpect(status().isOk())
		.andExpect(content()
				.contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
		.andExpect(jsonPath("$.status", is("OK")));
		//@formatter:on
	}

	@After
	public void tearDown() {

		log.debug("--------------------------------");
	}
}
