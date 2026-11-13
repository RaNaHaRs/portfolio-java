package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.Name;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.certifications")
public record CertificationsContentProperties(@NotEmpty List<@Valid Certification> items) {

	public record Certification(
			@NotBlank String title,
			@NotBlank String issuer,
			@Positive Integer year,
			@Name("public")
			boolean isPublic,
			String file) {
	}

}
