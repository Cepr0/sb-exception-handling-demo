package io.github.cepr0.common;

import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.Validator;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig {
	@Bean
	public WebMvcConfigurer webMvcConfigurer(LocalValidatorFactoryBean validatorFactoryBean, HierarchicalMessageSource messageSource) {
		return new WebMvcConfigurer() {
			@Override
			public Validator getValidator() {
				validatorFactoryBean.setValidationMessageSource(messageSource);
				return validatorFactoryBean;
			}
		};
	}
}
