package com.harshrana.portfolio.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;

@Controller
public class ContactController {

	private final ProfileContentProperties profile;

	private final SeoContentProperties seo;

	private final String baseUrl;

	public ContactController(
			ProfileContentProperties profile,
			SeoContentProperties seo,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.profile = profile;
		this.seo = seo;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping("/contact")
	String contact(Model model) {
		SeoContentProperties.Page metadata = seo.pages().stream()
				.filter(page -> page.route().equals("/contact"))
				.map(SeoContentProperties.PageEntry::metadata)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Missing SEO metadata for route /contact."));

		model.addAttribute("pageTitle", metadata.title());
		model.addAttribute("pageDescription", metadata.description());
		model.addAttribute("canonicalUrl", baseUrl + "/contact");
		model.addAttribute("openGraphImageUrl", metadata.openGraphImage());
		model.addAttribute("profile", profile);
		return "pages/contact";
	}

}
