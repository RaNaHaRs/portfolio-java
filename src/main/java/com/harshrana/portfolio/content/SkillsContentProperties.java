package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.skills")
public record SkillsContentProperties(
		boolean levelsConfirmed,
		@NotEmpty List<@Valid Category> categories) {

	public record Category(
			@NotBlank String name,
			@NotEmpty List<@Valid Skill> skills) {
	}

	public record Skill(
			@NotBlank String name,
			String level,
			List<@NotBlank String> usedIn) {
	}

}
