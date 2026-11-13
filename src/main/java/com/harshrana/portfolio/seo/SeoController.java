package com.harshrana.portfolio.seo;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.harshrana.portfolio.content.SeoContentProperties;
import com.harshrana.portfolio.web.ProjectContentService;

@RestController
public class SeoController {

	private static final String SITEMAP_NAMESPACE = "http://www.sitemaps.org/schemas/sitemap/0.9";

	private final String baseUrl;

	private final ProjectContentService projects;

	private final SeoContentProperties seo;

	public SeoController(
			ProjectContentService projects,
			SeoContentProperties seo,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.projects = projects;
		this.seo = seo;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping(value = "/sitemap.xml", produces = MediaType.APPLICATION_XML_VALUE)
	public ResponseEntity<String> sitemap() {
		List<String> locations = new java.util.ArrayList<>();
		seo.pages().stream()
				.map(SeoContentProperties.PageEntry::route)
				.map(this::absoluteUrl)
				.forEach(locations::add);
		projects.visibleProjects().stream()
				.map(project -> absoluteUrl("/projects/" + project.project().slug()))
				.forEach(locations::add);

		StringBuilder xml = new StringBuilder(256)
				.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>")
				.append("<urlset xmlns=\"").append(SITEMAP_NAMESPACE).append("\">");
		for (String location : locations) {
			xml.append("<url><loc>").append(escapeXml(location)).append("</loc></url>");
		}
		xml.append("</urlset>");
		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_XML)
				.body(xml.toString());
	}

	@GetMapping(value = "/robots.txt", produces = MediaType.TEXT_PLAIN_VALUE)
	public String robots() {
		return "User-agent: *\nAllow: /\nSitemap: " + baseUrl + "/sitemap.xml\n";
	}

	private String absoluteUrl(String route) {
		return route.equals("/") ? baseUrl + "/" : baseUrl + route;
	}

	private static String escapeXml(String value) {
		return value.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

}
