package com.harshrana.portfolio.web;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import com.harshrana.portfolio.content.ProjectsContentProperties;
import com.harshrana.portfolio.content.ProjectsContentProperties.Project;
import com.harshrana.portfolio.content.ProjectsContentProperties.Status;

@Service
public class ProjectContentService {

	private static final Comparator<ProjectPage> PROJECT_ORDER = Comparator.comparingInt(
			page -> page.project().order());

	private final CaseStudyMarkdownRenderer markdownRenderer = new CaseStudyMarkdownRenderer();

	private final boolean showInProgress;

	private final Map<String, ProjectPage> projectsBySlug;

	public ProjectContentService(ProjectsContentProperties properties, ResourceLoader resourceLoader) {
		this.showInProgress = properties.showInProgress();
		Map<String, ProjectPage> loadedProjects = new LinkedHashMap<>();
		for (Project project : properties.items()) {
			if (project.slug() == null || !project.slug().matches("[a-z0-9]+(?:-[a-z0-9]+)*")) {
				throw new IllegalStateException("Invalid project slug in portfolio content.");
			}
			ProjectPage previous = loadedProjects.putIfAbsent(project.slug(), load(project, resourceLoader));
			if (previous != null) {
				throw new IllegalStateException("Duplicate project slug: " + project.slug());
			}
		}
		this.projectsBySlug = Map.copyOf(loadedProjects);
	}

	public List<ProjectPage> visibleProjects() {
		return projectsBySlug.values().stream()
				.filter(page -> isVisible(page.project()))
				.sorted(PROJECT_ORDER)
				.toList();
	}

	public Optional<ProjectPage> findVisible(String slug) {
		ProjectPage project = projectsBySlug.get(slug);
		return project != null && isVisible(project.project()) ? Optional.of(project) : Optional.empty();
	}

	public Optional<ProjectPage> nextProject(String slug) {
		List<ProjectPage> visible = visibleProjects();
		if (visible.size() < 2) {
			return Optional.empty();
		}
		for (int index = 0; index < visible.size(); index++) {
			if (visible.get(index).project().slug().equals(slug)) {
				return Optional.of(visible.get((index + 1) % visible.size()));
			}
		}
		return Optional.empty();
	}

	private boolean isVisible(Project project) {
		return project.status() == Status.LIVE || (showInProgress && project.status() == Status.IN_PROGRESS);
	}

	private ProjectPage load(Project project, ResourceLoader resourceLoader) {
		String caseStudyPath = project.caseStudy();
		if (caseStudyPath == null || !caseStudyPath.matches("projects/[a-z0-9]+(?:-[a-z0-9]+)*\\.md")) {
			throw new IllegalStateException("Invalid case-study path for project " + project.slug() + ".");
		}
		Resource resource = resourceLoader.getResource("classpath:content/" + caseStudyPath);
		String markdown;
		try (var input = resource.getInputStream()) {
			markdown = new String(input.readAllBytes(), StandardCharsets.UTF_8);
		}
		catch (IOException exception) {
			throw new IllegalStateException(
					"Could not load case study for project " + project.slug() + " from " + caseStudyPath + ".",
					exception);
		}

		CaseStudyMarkdownRenderer.RenderedCaseStudy rendered = markdownRenderer.render(markdown, project.title());
		return new ProjectPage(project, rendered.html(), rendered.tableOfContents());
	}

	public record ProjectPage(Project project, String caseStudyHtml, List<TocHeading> tableOfContents) {
	}

	public record TocHeading(String label, String id, int level) {
	}

}
