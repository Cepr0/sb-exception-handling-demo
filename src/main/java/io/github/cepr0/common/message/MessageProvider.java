package io.github.cepr0.common.message;

import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.lang.NonNull;

import java.util.Locale;

public class MessageProvider {

	private static final String MSG_CODE_NOT_FOUND = "message.code-not-found";

	private final HierarchicalMessageSource messageSource;

	public MessageProvider(@NonNull final HierarchicalMessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@NonNull
	public String getLocalizedMessage(@NonNull String codeOrMessage, Object... args) {
		return messageSource.getMessage(
				codeOrMessage,
				args,
				String.format(codeOrMessage, args), // provide a default message - in case when the 'codeOrMessage' can't be resolved
				LocaleContextHolder.getLocale() // provide the user locale
		);
	}

	@NonNull
	public String getLocalizedMessage(@NonNull MessageSourceResolvable resolvable) {
		try {
			return messageSource.getMessage(resolvable, LocaleContextHolder.getLocale());
		} catch (NoSuchMessageException e) {
			return getLocalizedMessage(MSG_CODE_NOT_FOUND);
		}
	}

	public String getMessage(String codeOrMessage, Object... args) {
		return messageSource.getMessage(
				codeOrMessage,
				args,
				String.format(codeOrMessage, args),
				Locale.getDefault()
		);
	}

	public String getMessage(MessageSourceResolvable resolvable) {
		try {
			return messageSource.getMessage(resolvable, Locale.getDefault());
		} catch (NoSuchMessageException e) {
			return getMessage(MSG_CODE_NOT_FOUND);
		}
	}
}
