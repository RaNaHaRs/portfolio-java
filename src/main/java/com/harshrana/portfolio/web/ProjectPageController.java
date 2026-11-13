package com.harshrana.portfolio.web;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.server.ResponseStatusException;

import com.harshrana.portfolio.content.SeoContentProperties;

@Controller
public class ProjectPageController {

	private final ProjectContentService projects;

	private final SeoContentProperties seo;

	private final String baseUrl;

	public ProjectPageController(
			ProjectContentService projects,
			SeoContentProperties seo,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.projects = projects;
		this.seo = seo;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping("/projects")
	String projects(Model model) {
		SeoContentProperties.Page metadata = metadataFor("/projects");
		model.addAttribute("pageTitle", metadata.title());
		model.addAttribute("pageDescription", metadata.description());
		model.addAttribute("canonicalUrl", baseUrl + "/projects");
		model.addAttribute("openGraphImageUrl", metadata.openGraphImage());
		model.addAttribute("projects", this.projects.visibleProjects());
		return "pages/projects";
	}

	@GetMapping("/projects/{slug}")
	String project(@PathVariable String slug, Model model) {
		ProjectContentService.ProjectPage projectPage = projects.findVisible(slug)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		SeoContentProperties.Page projectMetadata = new SeoContentProperties.Page(
				projectPage.project().title(),
				projectPage.project().summary(),
				baseUrl + "/img/og-default.svg");
		model.addAttribute("pageTitle", projectMetadata.title());
		model.addAttribute("pageDescription", projectMetadata.description());
		model.addAttribute("canonicalUrl", baseUrl + "/projects/" + projectPage.project().slug());
		model.addAttribute("openGraphImageUrl", projectMetadata.openGraphImage());
		model.addAttribute("projectPage", projectPage);
		model.addAttribute("nextProject", projects.nextProject(slug).orElse(null));
		model.addAttribute("highlightCode", true);
		return "pages/project-detail";
	}

	private SeoContentProperties.Page metadataFor(String route) {
		List<SeoContentProperties.PageEntry> pages = seo.pages();
		return pages.stream()
				.filter(page -> page.route().equals(route))
				.map(SeoContentProperties.PageEntry::metadata)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Missing SEO metadata for route " + route + "."));
	}

}
