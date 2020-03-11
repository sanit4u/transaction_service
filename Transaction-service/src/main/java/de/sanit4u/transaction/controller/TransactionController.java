package de.sanit4u.transaction.controller;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.sanit4u.transaction.controller.response.RestResponse;
import de.sanit4u.transaction.controller.validator.TransactionValidator;
import de.sanit4u.transaction.dto.SumDTO;
import de.sanit4u.transaction.model.Transaction;
import de.sanit4u.transaction.service.TransactionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/transactionservice")
@Api(value = "transaction", description = "Transaction Service Rest API end point", tags = { "transaction" })
public class TransactionController {

	protected final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private TransactionService transactionService;

	/**
	 * Initial Binder for binding the object validator
	 * 
	 * @param binder
	 */
	@InitBinder("transaction")
	public void initBinder(WebDataBinder binder) {
		binder.setValidator(new TransactionValidator());
	}

	@ApiOperation(value = "Records transaction")
	@PutMapping(path = "/transaction/{transactionId}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> recordTransaction(@PathVariable @NotBlank @Min(1) long transactionId,
			@Valid @RequestBody Transaction transaction) {

		log.debug(String.format("recording Transaction for %d ", transactionId));

		transaction.setId(transactionId);

		transactionService.recordTransaction(transaction);

		return ResponseEntity.ok().body(new RestResponse(HttpStatus.OK));

	}

	@ApiOperation(value = "Retrieve transaction by Transaction Id")
	@GetMapping(path = "/transaction/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findTransactionById(@PathVariable("transactionId") @NotBlank @Min(1) long transactionId) {
		log.debug(String.format("retrieving Transaction for %d ", transactionId));

		Transaction transaction = transactionService.retrieveTransaction(transactionId);

		return ResponseEntity.ok().body(transaction);

	}

	@ApiOperation(value = "Retrieve all transactions by the type")
	@GetMapping(path = "/types/{type}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> findTransactionByType(@PathVariable @NotBlank @Size(min = 1) String type) {
		log.debug(String.format("retrieving all transactions for %s ", type));

		List<Long> transactionIds = transactionService.retrieveTransactionByType(type);

		return ResponseEntity.ok().body(transactionIds);
	}

	@ApiOperation(value = "Returns sum of all transactions transitively linked by parentId")
	@GetMapping(path = "/sum/{transactionId}", produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<?> sumChildTransactionsById(@PathVariable @NotBlank @Min(1) long transactionId) {
		log.debug(String.format("retrieving all transactions for %d ", transactionId));

		SumDTO sumDTO = transactionService.sumTransaction(transactionId);

		return ResponseEntity.ok().body(sumDTO);
	}

}
