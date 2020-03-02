package de.sanit4u.transaction.controller.validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import de.sanit4u.transaction.model.Transaction;

public class TransactionValidator implements Validator {
	private static final Logger log = LoggerFactory.getLogger(TransactionValidator.class);

	@Override
	public boolean supports(final Class<?> clazz) {
		return Transaction.class.equals(clazz);
	}

	@Override
	public void validate(final Object profile, final Errors errors) {
		log.debug("validating the request");
		ValidationUtils.rejectIfEmptyOrWhitespace(errors, "type", "Type should not be empty or null");
	}

}
