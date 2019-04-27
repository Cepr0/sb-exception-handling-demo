package io.github.cepr0.common.error;

import lombok.Getter;
import org.springframework.validation.Errors;

public class ValidationException extends RuntimeException {
	
	@Getter
	private final Errors errors;
	
	public ValidationException(String message, Errors errors) {
		super(message);
		this.errors = errors;
	}
}
