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
import com.harshrana.portfolio.content.SeoContentProperties;

@WebMvcTest(controllers = ContactController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	HtmlErrorPageExceptionHandler.class,
	ContactPageControllerTests.ContentConfiguration.class
})
class ContactPageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersConfiguredContactCardsWithoutInventingEmailOrAddingThePhaseEForm() throws Exception {
		String html = mockMvc.perform(get("/contact").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080/contact");
		assertThat(html)
				.contains("<title>Contact")
				.contains("<link rel=\"canonical\" href=\"http://localhost:8080/contact\">")
				.contains("Let’s connect")
				.contains("LinkedIn")
				.contains("https://www.linkedin.com/in/harsh-rana-ba792a296/")
				.contains("GitHub")
				.contains("https://github.com/RaNaHaRs/")
				.contains("href=\"/resume\"")
				.contains("Download resume")
				.doesNotContain("mailto:")
				.doesNotContain("<form");
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties({ ProfileContentProperties.class, SeoContentProperties.class })
	static class ContentConfiguration {
	}

}
