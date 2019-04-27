package io.github.cepr0.demo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class Model {
	@NotNull private Integer num;
	@NotEmpty private String text;
}
