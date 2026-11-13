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
import com.harshrana.portfolio.content.SkillsContentProperties;

@WebMvcTest(controllers = SkillsController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	HtmlErrorPageExceptionHandler.class,
	ProjectContentService.class,
	SkillsPageControllerTests.ContentConfiguration.class
})
class SkillsPageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersCategorizedSkillListWithVisibleProjectEvidenceAndNoUnconfirmedLevels() throws Exception {
		String html = mockMvc.perform(get("/skills").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080/skills");
		assertThat(html)
				.contains("<title>Skills")
				.contains("<link rel=\"canonical\" href=\"http://localhost:8080/skills\">")
				.contains("These skill levels are proposed and still need confirmation.")
				.contains("Proposed: Strong")
				.contains("Proposed: Working knowledge")
				.contains("Proposed: Learning")
				.contains("Programming languages")
				.contains("Backend technologies")
				.contains("Databases")
				.contains("Java")
				.contains("No published project linked yet.")
				.contains("href=\"/projects/rentify\"")
				.contains("href=\"/projects/recoverlab\"")
				.doesNotContain("MovieSearch Recommendation Engine");
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties({
		ProfileContentProperties.class,
		ProjectsContentProperties.class,
		SeoContentProperties.class,
		SkillsContentProperties.class
	})
	static class ContentConfiguration {
	}

}
