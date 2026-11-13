package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.home")
public record HomeContentProperties(
		@NotEmpty List<@Valid ProofItem> proofItems,
		@NotEmpty List<@Valid JourneyStop> journeyStops) {

	public record ProofItem(
			@NotBlank String label,
			@NotBlank String detail,
			@Pattern(regexp = "^/.*") String targetRoute,
			String statusChip,
			boolean approvedForDisplay) {
	}

	public record JourneyStop(
			@NotBlank String label,
			@NotBlank String teaser,
			@NotBlank @Pattern(regexp = "^/.*") String targetRoute,
			@NotBlank String statusChip) {
	}

}
