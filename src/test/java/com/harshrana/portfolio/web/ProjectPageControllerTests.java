package com.harshrana.portfolio.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.harshrana.portfolio.PageContractAssertions;
import com.harshrana.portfolio.config.CspNonceModelAdvice;
import com.harshrana.portfolio.config.SecurityConfiguration;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.ProjectsContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;

@WebMvcTest(controllers = ProjectPageController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	HtmlErrorPageExceptionHandler.class,
	ProjectContentService.class,
	ProjectPageControllerTests.ContentConfiguration.class
})
class ProjectPageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersVisibleProjectsInOrderAndDoesNotListUnconfirmedInProgressWork() throws Exception {
		String html = mockMvc.perform(get("/projects").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080/projects");
		assertThat(html)
				.contains("<title>Projects · Harsh Rana, Java Backend Developer</title>")
				.contains("Rentify")
				.contains("RecoverLab CRM")
				.contains("Internship contribution; not a solo project")
				.doesNotContain("MovieSearch Recommendation Engine");
		assertThat(html.indexOf("Rentify")).isLessThan(html.indexOf("RecoverLab CRM"));
	}

	@Test
	void rendersCaseStudyWithGeneratedContentsAnchorsAndCodeHighlighter() throws Exception {
		String html = mockMvc.perform(get("/projects/rentify").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080/projects/rentify");
		assertThat(html)
				.contains("<title>Rentify · Harsh Rana, Java Backend Developer</title>")
				.contains("<h1>Rentify</h1>")
				.contains("href=\"#problem\"")
				.contains("<h2 id=\"problem\">Problem</h2>")
				.contains("/js/vendor/highlight.min-")
				.contains("Live demo")
				.contains("Next: RecoverLab CRM");
	}

	@Test
	void rendersRecoverLabCaseStudyWithThePageContract() throws Exception {
		String html = mockMvc.perform(get("/projects/recoverlab").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080/projects/recoverlab");
		assertThat(html)
				.contains("<title>RecoverLab CRM · Harsh Rana, Java Backend Developer</title>")
				.contains("Internship contribution; not a solo project");
	}

	@Test
	void unknownAndNotYetVisibleProjectSlugsReturnCustomNotFoundPage() throws Exception {
		for (String slug : new String[] { "unknown", "moviesearch" }) {
			String html = mockMvc.perform(get("/projects/" + slug).accept(MediaType.TEXT_HTML))
					.andExpect(status().isNotFound())
					.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
					.andReturn()
					.getResponse()
					.getContentAsString();

			assertThat(html).contains("404: this route isn’t mapped.");
		}
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties({
		ProfileContentProperties.class,
		ProjectsContentProperties.class,
		SeoContentProperties.class
	})
	static class ContentConfiguration {
	}

}
