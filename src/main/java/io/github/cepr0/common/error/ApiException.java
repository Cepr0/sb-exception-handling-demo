package io.github.cepr0.common.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;


public class ApiException extends RuntimeException {

	@Getter private final HttpStatus httpStatus;
	private final String localizedMessage;

	public ApiException(@NonNull final HttpStatus httpStatus, @NonNull final String message, @NonNull final String localizedMessage, @NonNull final Throwable cause) {
		super(message, cause);
		this.httpStatus = httpStatus;
		this.localizedMessage = localizedMessage;
	}

	public ApiException(@NonNull final HttpStatus httpStatus, @NonNull final String message, @NonNull final String localizedMessage) {
		super(message);
		this.httpStatus = httpStatus;
		this.localizedMessage = localizedMessage;
	}

	@Override
	public String getLocalizedMessage() {
		return localizedMessage;
	}
}
