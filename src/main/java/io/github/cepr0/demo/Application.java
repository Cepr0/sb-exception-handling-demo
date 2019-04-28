package io.github.cepr0.demo;

import io.github.cepr0.common.error.ApiExceptionFactory;
import io.github.cepr0.common.error.ExceptionsHandler;
import io.github.cepr0.common.message.MessageProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.validation.Valid;
import java.util.Locale;

import static io.github.cepr0.common.error.ApiErrorMessage.badRequest;
import static io.github.cepr0.common.error.ApiErrorMessage.notFound;

@RestController
@RequestMapping("demo")
@SpringBootApplication
public class Application {

	private final ApiExceptionFactory apiError;
	private final MessageProvider mp;

	public Application(ApiExceptionFactory apiError, ExceptionsHandler exceptionsHandler, MessageProvider mp) {
		this.apiError = apiError;
		this.mp = mp;

		// HttpMessageNotReadableException custom handler
		exceptionsHandler.addHandler(
				HttpMessageNotReadableException.class,
				ex -> badRequest(mp.getLocalizedMessage("request.invalid-body"))
		);

		// https://stackoverflow.com/a/48312952
		// NoHandlerFoundException custom handler
		exceptionsHandler.addHandler(
				NoHandlerFoundException.class,
				ex -> notFound(mp.getLocalizedMessage("request.path-unsupported"))
		);
	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		Locale.setDefault(Locale.US);
	}

	@PostMapping
	public Model post(@Valid @RequestBody @NonNull final Model model) {

		if (model.getNum() == 0) {
			throw apiError.forbidden("model.forbidden-id");
		}

		if (model.getText().length() < 4) {
			throw new ResponseStatusException(HttpStatus.FORBIDDEN, mp.getLocalizedMessage("model.short-text"));
		}

		if (model.getText().length() > 4) {
			throw new InvalidPropertyException(mp.getLocalizedMessage("model.big-text"));
		}

		return model;
	}

	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	static class InvalidPropertyException extends RuntimeException {
		InvalidPropertyException(final String message) {
			super(message);
		}
	}
}
