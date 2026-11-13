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
import com.harshrana.portfolio.content.AboutContentProperties;
import com.harshrana.portfolio.content.CertificationsContentProperties;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;
import com.harshrana.portfolio.content.TimelineContentProperties;

@WebMvcTest(controllers = AboutController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	HtmlErrorPageExceptionHandler.class,
	AboutPageControllerTests.ContentConfiguration.class
})
class AboutPageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersStoryTimelineEducationCertificationsAndMottoWithoutUnapprovedItems() throws Exception {
		String html = mockMvc.perform(get("/about").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080/about");
		assertThat(html)
				.contains("<title>About")
				.contains("<link rel=\"canonical\" href=\"http://localhost:8080/about\">")
				.contains("After Class 12")
				.contains("Internship at Selkey Cybersecurity")
				.contains("href=\"/about\"")
				.contains("A.D. Patel Institute of Technology")
				.contains("Expected 2027")
				.contains("GeeksforGeeks Java Certification")
				.contains("Internship Completion Certificate")
				.contains("Smart India Hackathon 2025 Participation Certificate")
				.contains("Curiosity started my coding journey")
				.contains("Upcoming")
				.doesNotContain("MovieSearch Recommendation Engine")
				.doesNotContain("Hyperlink InfoSystem")
				.doesNotContain("7.82")
				.doesNotContain("View certificate");
		assertThat(html.indexOf("Programming vacation batch"))
				.isLessThan(html.indexOf("Built Rentify"));
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties({
		AboutContentProperties.class,
		CertificationsContentProperties.class,
		ProfileContentProperties.class,
		SeoContentProperties.class,
		TimelineContentProperties.class
	})
	static class ContentConfiguration {
	}

}
