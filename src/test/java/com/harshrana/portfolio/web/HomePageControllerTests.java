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
import com.harshrana.portfolio.content.HomeContentProperties;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.ProjectsContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;

@WebMvcTest(controllers = HomeController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	HtmlErrorPageExceptionHandler.class,
	ProjectContentService.class,
	HomePageControllerTests.ContentConfiguration.class
})
class HomePageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersHomePageWithApprovedProofStaticJourneyAndVisibleFeaturedProjects() throws Exception {
		String html = mockMvc.perform(get("/").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080");
		assertThat(html)
				.contains("<title>Home")
				.contains("<link rel=\"canonical\" href=\"http://localhost:8080\">")
				.contains("property=\"og:image\" content=\"http://localhost:8080/img/og-default.svg\"")
				.contains("type=\"application/ld+json\"")
				.contains("\"@type\": \"Person\"")
				.contains("\"jobTitle\": \"Java Backend Developer\"")
				.contains("Become a Java Specialist")
				.contains("Hackathons")
				.contains("Production CRM")
				.contains("href=\"/about\"")
				.contains("href=\"/skills\"")
				.contains("href=\"/experience\"")
				.contains("href=\"/projects\"")
				.contains("href=\"/contact\"")
				.contains("Curiosity started my coding journey")
				.contains("Rentify")
				.contains("RecoverLab CRM")
				.doesNotContain("Placement wording to confirm")
				.doesNotContain("MovieSearch Recommendation Engine");
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties({
		HomeContentProperties.class,
		ProfileContentProperties.class,
		ProjectsContentProperties.class,
		SeoContentProperties.class
	})
	static class ContentConfiguration {
	}

}
