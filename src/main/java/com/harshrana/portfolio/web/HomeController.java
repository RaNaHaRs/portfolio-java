package com.harshrana.portfolio.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.harshrana.portfolio.content.HomeContentProperties;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;

@Controller
public class HomeController {

	private final ProfileContentProperties profile;

	private final HomeContentProperties home;

	private final SeoContentProperties seo;

	private final ProjectContentService projects;

	private final String baseUrl;

	public HomeController(
			ProfileContentProperties profile,
			HomeContentProperties home,
			SeoContentProperties seo,
			ProjectContentService projects,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.profile = profile;
		this.home = home;
		this.seo = seo;
		this.projects = projects;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping("/")
	String home(Model model) {
		SeoContentProperties.Page metadata = metadataForHome();
		List<ProjectContentService.ProjectPage> featuredProjects = projects.visibleProjects().stream()
				.limit(3)
				.toList();
		List<String> snapshotStack = featuredProjects.stream()
				.flatMap(project -> project.project().stack().stream())
				.distinct()
				.limit(4)
				.toList();

		model.addAttribute("pageTitle", metadata.title());
		model.addAttribute("pageDescription", metadata.description());
		model.addAttribute("canonicalUrl", baseUrl);
		model.addAttribute("openGraphImageUrl", metadata.openGraphImage());
		model.addAttribute("profile", profile);
		model.addAttribute("proofItems", home.proofItems().stream()
				.filter(HomeContentProperties.ProofItem::approvedForDisplay)
				.toList());
		model.addAttribute("journeyStops", home.journeyStops());
		model.addAttribute("featuredProjects", featuredProjects);
		model.addAttribute("snapshotStack", snapshotStack);
		return "pages/home";
	}

	private SeoContentProperties.Page metadataForHome() {
		return seo.pages().stream()
				.filter(page -> page.route().equals("/"))
				.map(SeoContentProperties.PageEntry::metadata)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Missing SEO metadata for route /."));
	}

}
