package com.harshrana.portfolio.web;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.harshrana.portfolio.content.AboutContentProperties;
import com.harshrana.portfolio.content.CertificationsContentProperties;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;
import com.harshrana.portfolio.content.TimelineContentProperties;

@Controller
public class AboutController {

	private final AboutContentProperties about;

	private final CertificationsContentProperties certifications;

	private final ProfileContentProperties profile;

	private final SeoContentProperties seo;

	private final TimelineContentProperties timeline;

	private final String baseUrl;

	public AboutController(
			AboutContentProperties about,
			CertificationsContentProperties certifications,
			ProfileContentProperties profile,
			SeoContentProperties seo,
			TimelineContentProperties timeline,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.about = about;
		this.certifications = certifications;
		this.profile = profile;
		this.seo = seo;
		this.timeline = timeline;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping("/about")
	String about(Model model) {
		SeoContentProperties.Page metadata = seo.pages().stream()
				.filter(page -> page.route().equals("/about"))
				.map(SeoContentProperties.PageEntry::metadata)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Missing SEO metadata for route /about."));

		model.addAttribute("pageTitle", metadata.title());
		model.addAttribute("pageDescription", metadata.description());
		model.addAttribute("canonicalUrl", baseUrl + "/about");
		model.addAttribute("openGraphImageUrl", metadata.openGraphImage());
		model.addAttribute("story", about.story());
		model.addAttribute("timeline", timeline.events().stream()
				.filter(TimelineContentProperties.Event::approvedForDisplay)
				.toList());
		model.addAttribute("education", profile.education());
		model.addAttribute("showCgpa", profile.flags().showCgpa());
		model.addAttribute("location", profile.location());
		model.addAttribute("careerGoal", profile.careerGoal());
		model.addAttribute("certifications", certifications.items());
		model.addAttribute("motto", profile.motto());
		return "pages/about";
	}

}
