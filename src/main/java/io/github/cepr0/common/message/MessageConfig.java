package io.github.cepr0.common.message;

import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

@Configuration
public class MessageConfig {

	@Bean
	MessageProvider messageProvider(HierarchicalMessageSource messageSource) {

		ResourceBundleMessageSource bundleMessageSource = new ResourceBundleMessageSource();
		bundleMessageSource.setBasename("common-messages");
		messageSource.setParentMessageSource(bundleMessageSource);

		return new MessageProvider(messageSource);
	}
}
