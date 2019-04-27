package io.github.cepr0.common.error;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;

/**
 * Error container which is rendered as:<br/>
 * <pre>
 * {
 *    "timestamp": "2017-10-20T13:36:04.859Z",
 *    "status": 400,
 *    "error": "Bad request",
 *    "message": "Validation failed",
 *    "path": "/path",
 *    "errors": [
 *       {
 *          "message": "conditions[1].value must be at least 0!",
 *          "object": "Validation",
 *          "property": "conditions[1].value",
 *          "invalidValue": -10
 *       },
 *       {
 *          "message": "conditions[2].value must be at least 0!",
 *          "object": "Validation",
 *          "property": "conditions[2].value",
 *          "invalidValue": -20
 *       }
 *    ]
 * }
 * </pre>
 *
 */
@JsonInclude(NON_EMPTY)
@JsonPropertyOrder({"timestamp", "status", "error", "message", "path", "errors"})
@Value
public class ApiErrorMessage {
	
	/**
	 * Error's timestamp
	 */
	private Instant timestamp;

	@JsonIgnore
	private HttpStatus httpStatus;

	/**
	 * Error's HTTP status
	 */
	private Integer status;
	
	/**
	 * Error's status description
	 */
	private String error;
	
	/**
	 * Error's detailed message
	 */
	private String message;
	
	/**
	 * Request path related to the error
	 */
	private String path;
	
	/**
	 * Collection of detailed sub-errors, for example, validation error of the request body specific field
	 */
	private Collection<Error> errors = new ArrayList<>();

	@Builder
	private ApiErrorMessage(final Instant timestamp, final HttpStatus httpStatus, final String message, final String path) {
		this.timestamp = timestamp != null ? timestamp : Instant.now();
		this.httpStatus = httpStatus != null ? httpStatus : HttpStatus.INTERNAL_SERVER_ERROR;
		this.status = this.httpStatus.value();
		this.error = this.httpStatus.getReasonPhrase();
		this.message = message;
		this.path = path != null ? path : ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getRequestURI();
	}

	@NonNull
	public ApiErrorMessage addError(@NonNull final Error error) {
		errors.add(error);
		return this;
	}

	@NonNull
	public static ApiErrorMessage internalServerError(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.INTERNAL_SERVER_ERROR)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage badRequest(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.BAD_REQUEST)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage notFound(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.NOT_FOUND)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage conflict(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.CONFLICT)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage unprocessableEntity(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.UNPROCESSABLE_ENTITY)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage forbidden(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.FORBIDDEN)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage unauthorized(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.UNAUTHORIZED)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage methodNotAllowed(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.METHOD_NOT_ALLOWED)
				.message(message)
				.build();
	}

	@NonNull
	public static ApiErrorMessage notAcceptable(@NonNull final String message) {
		return ApiErrorMessage.builder()
				.httpStatus(HttpStatus.NOT_ACCEPTABLE)
				.message(message)
				.build();
	}

	@JsonInclude(NON_EMPTY)
	@Value(staticConstructor = "of")
	public static class Error {
		
		/**
		 * Detailed sub-error message
		 */
		String message;
		
		/**
		 * Object name related to the sub-error
		 */
		String object;
		
		/**
		 * Object property name related to the sub-error
		 */
		String property;
		
		/**
		 * Invalid value of object property related to the sub-error
		 */
		Object invalidValue;

		@NonNull
		public static Error of(@NonNull final String message) {
			return new Error(message, null, null, null);
		}
		
		@NonNull
		public static Error of(@NonNull final String message, @NonNull final ObjectError err) {
			if (err instanceof FieldError) {
				return new Error(message, err.getObjectName(), ((FieldError) err).getField(), ((FieldError) err).getRejectedValue());
			} else {
				return new Error(message, err.getObjectName(), null, null);
			}
		}
	}
}
