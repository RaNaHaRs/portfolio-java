package com.harshrana.portfolio.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.harshrana.portfolio.content.ExperienceContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;

@Controller
public class ExperienceController {

	private final ExperienceContentProperties experience;

	private final SeoContentProperties seo;

	private final String baseUrl;

	public ExperienceController(
			ExperienceContentProperties experience,
			SeoContentProperties seo,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.experience = experience;
		this.seo = seo;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping("/experience")
	String experience(Model model) {
		SeoContentProperties.Page metadata = seo.pages().stream()
				.filter(page -> page.route().equals("/experience"))
				.map(SeoContentProperties.PageEntry::metadata)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Missing SEO metadata for route /experience."));
		List<ExperienceContentProperties.Position> positions = experience.positions();

		model.addAttribute("pageTitle", metadata.title());
		model.addAttribute("pageDescription", metadata.description());
		model.addAttribute("canonicalUrl", baseUrl + "/experience");
		model.addAttribute("openGraphImageUrl", metadata.openGraphImage());
		model.addAttribute("positions", positions);
		return "pages/experience";
	}

}
