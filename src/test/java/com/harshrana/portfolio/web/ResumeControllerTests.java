package com.harshrana.portfolio.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.harshrana.portfolio.config.SecurityConfiguration;
import com.harshrana.portfolio.content.ProfileContentProperties;

@WebMvcTest(controllers = ResumeController.class)
@Import({ SecurityConfiguration.class, ResumeControllerTests.ContentConfiguration.class })
class ResumeControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void streamsTheResumeAsAnAttachment() throws Exception {
		byte[] pdf = mockMvc.perform(get("/resume"))
				.andExpect(status().isOk())
				.andExpect(content().contentType(MediaType.APPLICATION_PDF))
				.andExpect(header().string(
						HttpHeaders.CONTENT_DISPOSITION,
						"attachment; filename=\"Harsh-Rana-Resume.pdf\""))
				.andReturn()
				.getResponse()
				.getContentAsByteArray();

		assertThat(pdf.length).isPositive();
		assertThat(new String(pdf, 0, 5, StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties(ProfileContentProperties.class)
	static class ContentConfiguration {
	}

}
