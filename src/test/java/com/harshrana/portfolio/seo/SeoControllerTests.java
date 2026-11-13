package com.harshrana.portfolio.seo;

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

import com.harshrana.portfolio.config.CspNonceModelAdvice;
import com.harshrana.portfolio.config.SecurityConfiguration;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.ProjectsContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;
import com.harshrana.portfolio.web.ProjectContentService;

@WebMvcTest(controllers = SeoController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	ProjectContentService.class,
	SeoControllerTests.ContentConfiguration.class
})
class SeoControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void sitemapIncludesPublicPagesAndVisibleProjectsOnly() throws Exception {
		String sitemap = mockMvc.perform(get("/sitemap.xml").accept(MediaType.APPLICATION_XML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_XML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		assertThat(sitemap)
				.contains("<loc>http://localhost:8080/</loc>")
				.contains("<loc>http://localhost:8080/about</loc>")
				.contains("<loc>http://localhost:8080/projects/rentify</loc>")
				.contains("<loc>http://localhost:8080/projects/recoverlab</loc>")
				.doesNotContain("moviesearch");
	}

	@Test
	void robotsListsTheConfiguredSitemapLocation() throws Exception {
		mockMvc.perform(get("/robots.txt"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_PLAIN))
				.andExpect(content().string(
						"User-agent: *\nAllow: /\nSitemap: http://localhost:8080/sitemap.xml\n"));
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
