package com.harshrana.portfolio.content;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.profile")
public record ProfileContentProperties(
		@NotBlank String name,
		@NotBlank String role,
		@NotBlank String tagline,
		String statement,
		@NotBlank String location,
		@Email String email,
		@Valid @NotNull Links links,
		@NotBlank String resumePath,
		String photo,
		@Valid @NotNull Education education,
		String careerGoal,
		String motto,
		@Valid @NotNull Flags flags) {

	public record Links(
			@NotBlank @URL String github,
			@NotBlank @URL String linkedIn) {
	}

	public record Education(
			@NotBlank String degree,
			@NotBlank String field,
			@NotBlank String institution,
			@NotBlank String university,
			@Valid @NotNull Graduation graduation,
			java.math.BigDecimal cgpa) {
	}

	public record Graduation(@NotNull Integer expectedYear) {
	}

	public record Flags(
			@NotNull Boolean showCgpa,
			@Valid @NotNull Contact contact) {
	}

	public record Contact(@Valid @NotNull ContactForm form) {
	}

	public record ContactForm(@NotNull Boolean enabled) {
	}

}
