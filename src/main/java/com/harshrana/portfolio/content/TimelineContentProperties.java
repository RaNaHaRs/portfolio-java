package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.timeline")
public record TimelineContentProperties(@NotEmpty List<@Valid Event> events) {

	public record Event(
			@Positive int year,
			String period,
			@NotBlank String title,
			@NotBlank String description,
			List<@NotBlank String> tags,
			boolean upcoming,
			boolean approvedForDisplay) {
	}

}
