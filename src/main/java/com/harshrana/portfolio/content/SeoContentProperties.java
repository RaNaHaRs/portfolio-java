package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.seo")
public record SeoContentProperties(
		@Valid @NotNull Page defaults,
		@NotEmpty List<@Valid PageEntry> pages) {

	public record PageEntry(
			@NotBlank String route,
			@Valid @NotNull Page metadata) {
	}

	public record Page(
			@NotBlank String title,
			@NotBlank String description,
			@URL String openGraphImage) {
	}

}
