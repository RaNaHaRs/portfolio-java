package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.experience")
public record ExperienceContentProperties(@NotEmpty List<@Valid Position> positions) {

	public record Position(
			@NotBlank String role,
			@NotBlank String organization,
			@NotBlank String location,
			@NotBlank String start,
			@NotBlank String end,
			@NotBlank String summary,
			@NotEmpty List<@NotBlank String> stack,
			@NotEmpty List<@NotBlank String> responsibilities,
			List<@Valid Recognition> recognitions) {
	}

	public record Recognition(
			@NotBlank String title,
			boolean approvedForPublicDisplay) {
	}

}
