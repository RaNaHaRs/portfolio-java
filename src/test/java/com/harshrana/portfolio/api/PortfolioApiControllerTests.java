package com.harshrana.portfolio.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.Instant;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.harshrana.portfolio.config.CspNonceModelAdvice;
import com.harshrana.portfolio.config.SecurityConfiguration;
import com.harshrana.portfolio.content.ExperienceContentProperties;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.ProjectsContentProperties;
import com.harshrana.portfolio.content.SkillsContentProperties;
import com.harshrana.portfolio.web.HtmlErrorPageExceptionHandler;
import com.harshrana.portfolio.web.ProjectContentService;

@WebMvcTest(controllers = PortfolioApiController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	HtmlErrorPageExceptionHandler.class,
	ProjectContentService.class,
	PortfolioApiControllerTests.ContentConfiguration.class
})
class PortfolioApiControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private PortfolioStatusService statusService;

	@BeforeEach
	void provideStatusResponse() {
		given(statusService.status()).willReturn(new PortfolioStatusService.StatusResponse(
				120,
				42,
				new PortfolioStatusService.JvmStatus(64, 256),
				new PortfolioStatusService.BuildStatus(
						"0.0.1-SNAPSHOT",
						"abc1234",
						Instant.parse("2026-11-13T21:00:00Z"))));
	}

	@Test
	void returnsPublicProfileWithoutHiddenCgpaOrInternalFlags() throws Exception {
		String json = mockMvc.perform(get("/api/v1/profile").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
				.andExpect(jsonPath("$.name").value("Harsh Rana"))
				.andExpect(jsonPath("$.links.github").value("https://github.com/RaNaHaRs/"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(json).doesNotContain("7.82", "showCgpa", "contact");
	}

	@Test
	void exposesOnlyVisibleProjectsAndReturnsProblemDetailsForHiddenSlugs() throws Exception {
		mockMvc.perform(get("/api/v1/projects").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.length()").value(2))
				.andExpect(jsonPath("$[0].slug").value("rentify"))
				.andExpect(jsonPath("$[1].slug").value("recoverlab"));

		mockMvc.perform(get("/api/v1/projects/recoverlab").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.project.title").value("RecoverLab CRM"))
				.andExpect(jsonPath("$.project.typeNote").value("Internship contribution; not a solo project"))
				.andExpect(jsonPath("$.caseStudyHtml").isNotEmpty());

		mockMvc.perform(get("/api/v1/projects/moviesearch").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(jsonPath("$.status").value(404));
	}

	@Test
	void returnsSkillsWithOnlyVisibleProjectAssociations() throws Exception {
		String json = mockMvc.perform(get("/api/v1/skills").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.levelsConfirmed").value(false))
				.andExpect(jsonPath("$.categories[0].name").value("Programming languages"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(json)
				.contains("\"slug\":\"rentify\"")
				.doesNotContain("moviesearch", "MovieSearch Recommendation Engine");
	}

	@Test
	void returnsExperienceWithoutUnapprovedRecognitionItems() throws Exception {
		String json = mockMvc.perform(get("/api/v1/experience").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].role").value("Web Development Intern"))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(json)
				.contains("\"recognitions\":[]")
				.doesNotContain("Internship appreciation letter");
	}

	@Test
	void returnsLiveStatusResponseShape() throws Exception {
		mockMvc.perform(get("/api/v1/status").accept(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.uptimeSeconds").value(120))
				.andExpect(jsonPath("$.requestsServed").value(42))
				.andExpect(jsonPath("$.jvm.usedMb").value(64))
				.andExpect(jsonPath("$.jvm.maxMb").value(256))
				.andExpect(jsonPath("$.build.version").value("0.0.1-SNAPSHOT"))
				.andExpect(jsonPath("$.build.commit").value("abc1234"))
				.andExpect(jsonPath("$.build.time").value("2026-11-13T21:00:00Z"));
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties({
		ExperienceContentProperties.class,
		ProfileContentProperties.class,
		ProjectsContentProperties.class,
		SkillsContentProperties.class
	})
	static class ContentConfiguration {
	}

}
