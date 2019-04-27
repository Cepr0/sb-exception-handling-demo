package io.github.cepr0.common.error;

import io.github.cepr0.common.message.MessageProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;

@Configuration
public class ApiExceptionFactory {

	private final MessageProvider mp;

	public ApiExceptionFactory(final MessageProvider mp) {
		this.mp = mp;
	}

	public ApiException with(@NonNull final HttpStatus httpStatus, @NonNull final String codeOrMessage, final Object... args) {
		return new ApiException(
				httpStatus,
				mp.getMessage(codeOrMessage, args),
				mp.getLocalizedMessage(codeOrMessage, args)
		);
	}

	public ApiException with(@NonNull final Throwable cause, @NonNull final HttpStatus httpStatus, @NonNull final String codeOrMessage, final Object... args) {
		return new ApiException(
				httpStatus,
				mp.getMessage(codeOrMessage, args),
				mp.getLocalizedMessage(codeOrMessage, args),
				cause
		);
	}

	public ApiException notFound(@NonNull final String codeOrMessage, final Object... args) {
		return with(HttpStatus.NOT_FOUND, codeOrMessage, args);
	}

	public ApiException conflict(@NonNull final String codeOrMessage, final Object... args) {
		return with(HttpStatus.CONFLICT, codeOrMessage, args);
	}

	public ApiException badRequest(@NonNull final String codeOrMessage, final Object... args) {
		return with(HttpStatus.BAD_REQUEST, codeOrMessage, args);
	}

	public ApiException internalServerError(@NonNull final String codeOrMessage, final Object... args) {
		return with(HttpStatus.INTERNAL_SERVER_ERROR, codeOrMessage, args);
	}

	public ApiException unprocessableEntity(@NonNull final String codeOrMessage, final Object... args) {
		return with(HttpStatus.UNPROCESSABLE_ENTITY, codeOrMessage, args);
	}

	public ApiException forbidden(@NonNull final String codeOrMessage, final Object... args) {
		return with(HttpStatus.FORBIDDEN, codeOrMessage, args);
	}

	public ApiException notFound(@NonNull final Throwable cause, @NonNull final String codeOrMessage, final Object... args) {
		return with(cause, HttpStatus.NOT_FOUND, codeOrMessage, args);
	}

	public ApiException conflict(@NonNull final Throwable cause, @NonNull final String codeOrMessage, final Object... args) {
		return with(cause, HttpStatus.CONFLICT, codeOrMessage, args);
	}

	public ApiException badRequest(@NonNull final Throwable cause, @NonNull final String codeOrMessage, final Object... args) {
		return with(cause, HttpStatus.BAD_REQUEST, codeOrMessage, args);
	}

	public ApiException internalServerError(@NonNull final Throwable cause, @NonNull final String codeOrMessage, final Object... args) {
		return with(cause, HttpStatus.INTERNAL_SERVER_ERROR, codeOrMessage, args);
	}

	public ApiException unprocessableEntity(@NonNull final Throwable cause, @NonNull final String codeOrMessage, final Object... args) {
		return with(cause, HttpStatus.UNPROCESSABLE_ENTITY, codeOrMessage, args);
	}

	public ApiException forbidden(@NonNull final Throwable cause, @NonNull final String codeOrMessage, final Object... args) {
		return with(cause, HttpStatus.FORBIDDEN, codeOrMessage, args);
	}

}
