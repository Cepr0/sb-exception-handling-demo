package io.github.cepr0.common.error;

import io.github.cepr0.common.message.MessageProvider;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.validation.ConstraintViolationException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

import static org.springframework.core.NestedExceptionUtils.getMostSpecificCause;
import static org.springframework.core.Ordered.HIGHEST_PRECEDENCE;

@Slf4j
@ControllerAdvice
@Configuration
public class ExceptionsHandler extends ResponseEntityExceptionHandler {

	private static final String VALIDATION_FAILED = "validation.failed";

	private final ExceptionHandlerMap exceptionHandlers = new ExceptionHandlerMap();
	private final MessageProvider mp;

	public ExceptionsHandler(final MessageProvider mp) {
		this.mp = mp;
		initHandlers();
	}

	public <E extends Exception> void addHandler(final Class<E> ex, final Function<E, ApiErrorMessage> handler) {
		exceptionHandlers.put(ex, handler);
	}

// ====================================================================================================================

	@NonNull
	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			@NonNull final Exception ex,
			final Object body,
			final HttpHeaders headers,
			final HttpStatus status,
			@NonNull final WebRequest request
	) {
		ApiErrorMessage errorMessage;

		var handler = exceptionHandlers.get(ex.getClass());
		if (handler != null) {
			logHandling(ex, (ServletWebRequest) request);
			errorMessage = handler.apply(ex);
		} else {
			log.error("[!] Not overridden exception: {}", ex.toString());
			errorMessage = ApiErrorMessage.builder()
					.httpStatus(status)
					.message(ex.getMessage())
					.build();
		}
		return super.handleExceptionInternal(ex, errorMessage, headers, errorMessage.getHttpStatus(), request);
	}

//	@Order(LOWEST_PRECEDENCE)
//	@ExceptionHandler(Exception.class)
//	ResponseEntity<?> handleException(Exception ex, ServletWebRequest request) {
//		log.error("[!] Unhandled exception: " + getMostSpecificCause(ex).toString(), ex);
//		return super.handleExceptionInternal(ex, internalServerError(ex.getMessage()), null, INTERNAL_SERVER_ERROR, request);
//	}

// ====================================================================================================================

	@Order(HIGHEST_PRECEDENCE)
	@ExceptionHandler(ApiException.class)
	ResponseEntity<?> handleException(ApiException ex, ServletWebRequest request) {
		logHandling(ex, request);
		var errorMessage = ApiErrorMessage.builder()
				.httpStatus(ex.getHttpStatus())
				.message(ex.getLocalizedMessage())
				.build();
		return super.handleExceptionInternal(ex, errorMessage, null, ex.getHttpStatus(), request);
	}

	@Order(HIGHEST_PRECEDENCE)
	@ExceptionHandler(ConstraintViolationException.class)
	ResponseEntity<?> handleException(ConstraintViolationException ex, ServletWebRequest request) {
		var errorMessage = ApiErrorMessage.unprocessableEntity(mp.getLocalizedMessage(VALIDATION_FAILED));
		ex.getConstraintViolations().forEach(error -> errorMessage.addError(ApiErrorMessage.Error.of(
				mp.getLocalizedMessage(error.getMessage()),
				error.getRootBeanClass().getSimpleName(),
				((PathImpl) error.getPropertyPath()).getLeafNode().asString(),
				error.getInvalidValue()
		)));
		return super.handleExceptionInternal(ex, errorMessage, null, errorMessage.getHttpStatus(), request);
	}

	@Order(HIGHEST_PRECEDENCE)
	@ExceptionHandler(ValidationException.class)
	ResponseEntity<?> handleException(ValidationException ex, ServletWebRequest request) {
		logHandling(ex, request);
		var errorMessage = ApiErrorMessage.unprocessableEntity(mp.getLocalizedMessage(VALIDATION_FAILED));
		ex.getErrors().getAllErrors().forEach(
				objectError -> errorMessage.addError(ApiErrorMessage.Error.of(mp.getLocalizedMessage(objectError), objectError))
		);
		return super.handleExceptionInternal(ex, errorMessage, null, errorMessage.getHttpStatus(), request);
	}

// --------------------------------------------------------------------------------------------------------------------

	private void initHandlers() {
		// MethodArgumentNotValidException
		addHandler(MethodArgumentNotValidException.class, ex -> {
			var errorMessage = ApiErrorMessage.unprocessableEntity(mp.getLocalizedMessage(VALIDATION_FAILED));
			ex.getBindingResult()
					.getAllErrors()
					.forEach(error -> errorMessage.addError(ApiErrorMessage.Error.of(mp.getLocalizedMessage(error), error)));
			return errorMessage;
		});
	}

// --------------------------------------------------------------------------------------------------------------------

	private void logHandling(@NonNull final Exception ex, @NonNull final ServletWebRequest request) {
		HttpMethod method = request.getHttpMethod();
		String path = request.getRequest().getRequestURI();
		Throwable e = getMostSpecificCause(ex);

		String cause = e.getCause() != null ? ". Cause: " + e.getCause().toString() : "";
		log.warn("[w] Request {} {} : " + e.getMessage() + cause, method, path);
	}

// --------------------------------------------------------------------------------------------------------------------

	private class ExceptionHandlerMap {

		final Map<Class<? extends Exception>, Function<? extends Exception, ApiErrorMessage>> map = new ConcurrentHashMap<>();

		<E extends Exception> void put(@NonNull final Class<E> key, @NonNull final Function<E, ApiErrorMessage> value) {
			map.put(key, value);
		}

		Function<Exception, ApiErrorMessage> get(@NonNull final Class<? extends Exception> key) {
			//noinspection unchecked
			return (Function<Exception, ApiErrorMessage>) map.get(key);
		}
	}
}
