package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.projects")
public record ProjectsContentProperties(boolean showInProgress, @NotEmpty List<@Valid Project> items) {

	public record Project(
			@NotBlank String slug,
			@Positive int order,
			@NotNull Status status,
			@NotBlank String title,
			@NotBlank String category,
			@NotBlank String typeNote,
			@NotBlank String summary,
			@NotEmpty List<@NotBlank String> stack,
			@Valid @NotNull Links links,
			List<@Valid Screenshot> screenshots,
			List<@NotBlank String> highlights,
			List<@Valid ApiEndpoint> apiSurface,
			String architectureDiagram,
			String erDiagram,
			@NotBlank String caseStudy) {
	}

	public record Links(@URL String live, @URL String repository) {
	}

	public record Screenshot(
			@NotBlank String src,
			@NotBlank String alt,
			@Positive int width,
			@Positive int height) {
	}

	public record ApiEndpoint(
			@NotBlank String method,
			@NotBlank String path,
			@NotBlank String purpose) {
	}

	public enum Status {
		LIVE,
		IN_PROGRESS,
		HIDDEN
	}

}
