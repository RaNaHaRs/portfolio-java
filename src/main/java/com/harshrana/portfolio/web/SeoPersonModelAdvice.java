package com.harshrana.portfolio.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.harshrana.portfolio.content.ProfileContentProperties;

@ControllerAdvice(assignableTypes = {
		HomeController.class,
		AboutController.class,
		SkillsController.class,
		ExperienceController.class,
		ProjectPageController.class,
		ContactController.class
})
public class SeoPersonModelAdvice {

	private final ProfileContentProperties profile;

	private final String baseUrl;

	public SeoPersonModelAdvice(
			ProfileContentProperties profile,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.profile = profile;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@ModelAttribute
	void addPersonStructuredData(Model model) {
		model.addAttribute("personJsonLd", new PersonJsonLd(
				profile.name(),
				profile.role(),
				baseUrl,
				List.of(profile.links().github(), profile.links().linkedIn())));
	}

	public record PersonJsonLd(String name, String role, String url, List<String> sameAs) {
	}

}
