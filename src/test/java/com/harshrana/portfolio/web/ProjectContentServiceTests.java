package com.harshrana.portfolio.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;

import com.harshrana.portfolio.content.ProjectsContentProperties;
import com.harshrana.portfolio.content.ProjectsContentProperties.Project;
import com.harshrana.portfolio.content.ProjectsContentProperties.Status;

class ProjectContentServiceTests {

	@Test
	void rendersHeadingsAsStableAnchorsAndEscapesEmbeddedHtml() {
		CaseStudyMarkdownRenderer renderer = new CaseStudyMarkdownRenderer();

		CaseStudyMarkdownRenderer.RenderedCaseStudy result = renderer.render("""
				# Project

				## Architecture

				## Architecture

				<script>alert('unsafe')</script>

				```java
				class Example {}
				```
				""", "Project");

		assertThat(result.html())
				.contains("<h2 id=\"architecture\">Architecture</h2>")
				.contains("<h2 id=\"architecture-2\">Architecture</h2>")
				.contains("&lt;script&gt;alert('unsafe')&lt;/script&gt;")
				.contains("<code class=\"language-java\">class Example {}\n</code>")
				.doesNotContain("<h2 id=\"project\">");
		assertThat(result.tableOfContents())
				.extracting(ProjectContentService.TocHeading::id)
				.containsExactly("architecture", "architecture-2");
	}

	@Test
	void showsInProgressOnlyWhenTheExplicitVisibilityFlagIsEnabled() throws IOException {
		ProjectsContentProperties configured = loadProjects();
		ProjectContentService defaultService = new ProjectContentService(configured, new DefaultResourceLoader());
		ProjectContentService previewService = new ProjectContentService(
				new ProjectsContentProperties(true, configured.items()),
				new DefaultResourceLoader());

		assertThat(defaultService.visibleProjects())
				.extracting(page -> page.project().slug())
				.containsExactly("rentify", "recoverlab");
		assertThat(defaultService.findVisible("moviesearch")).isEmpty();
		assertThat(previewService.visibleProjects())
				.extracting(page -> page.project().slug())
				.containsExactly("rentify", "recoverlab", "moviesearch");

		Project movieSearch = configured.items().get(2);
		Project hidden = new Project(
				"hidden-project",
				4,
				Status.HIDDEN,
				"Hidden project",
				movieSearch.category(),
				movieSearch.typeNote(),
				movieSearch.summary(),
				movieSearch.stack(),
				movieSearch.links(),
				movieSearch.screenshots(),
				movieSearch.highlights(),
				movieSearch.apiSurface(),
				movieSearch.architectureDiagram(),
				movieSearch.erDiagram(),
				movieSearch.caseStudy());
		ProjectContentService withHidden = new ProjectContentService(
				new ProjectsContentProperties(true, java.util.stream.Stream.concat(
						configured.items().stream(), java.util.stream.Stream.of(hidden)).toList()),
				new DefaultResourceLoader());
		assertThat(withHidden.findVisible("hidden-project")).isEmpty();
	}

	private static ProjectsContentProperties loadProjects() throws IOException {
		YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
		yaml.setResources(new ClassPathResource("content/projects.yml"));
		StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(
				new PropertiesPropertySource("projects", yaml.getObject()));
		return Binder.get(environment).bind("portfolio.content.projects", ProjectsContentProperties.class).get();
	}

}
