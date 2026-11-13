package com.harshrana.portfolio.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.regex.Pattern;

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
import com.harshrana.portfolio.content.ExperienceContentProperties;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.SeoContentProperties;
import com.harshrana.portfolio.PageContractAssertions;

@WebMvcTest(controllers = ExperienceController.class)
@Import({
	SecurityConfiguration.class,
	CspNonceModelAdvice.class,
	HtmlErrorPageExceptionHandler.class,
	ExperiencePageControllerTests.ContentConfiguration.class
})
class ExperiencePageControllerTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersInternshipDetailsAndResponsiveStaticBlueprints() throws Exception {
		String html = mockMvc.perform(get("/experience").accept(MediaType.TEXT_HTML))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn()
				.getResponse()
				.getContentAsString();

		PageContractAssertions.assertPageContract(html, "http://localhost:8080/experience");
		assertThat(html)
				.contains("<title>Experience")
				.contains("<link rel=\"canonical\" href=\"http://localhost:8080/experience\">")
				.contains("Web Development Intern")
				.contains("Selkey Cybersecurity Pvt. Ltd.")
				.contains("May 2026 – June 2026")
				.contains("Anand, Gujarat")
				.contains("Built responsive UI modules.")
				.contains("dynamic field configuration system")
				.contains("Internship completion certificate")
				.contains("recoverlab-blueprint-mobile")
				.contains("recoverlab-blueprint-");
	}

	@Test
	void servesBothBlueprintAssetsAsStaticSvgImages() throws Exception {
		String html = mockMvc.perform(get("/experience"))
				.andExpect(status().isOk())
				.andReturn()
				.getResponse()
				.getContentAsString();
		List<String> blueprintPaths = Pattern.compile("(?:src|srcset)=\"([^\"]*recoverlab-blueprint[^\"]*\\.svg)\"")
				.matcher(html)
				.results()
				.map(result -> result.group(1))
				.toList();

		assertThat(blueprintPaths).hasSize(2);
		for (String blueprintPath : blueprintPaths) {
			String svg = mockMvc.perform(get(blueprintPath))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.valueOf("image/svg+xml")))
					.andReturn()
					.getResponse()
					.getContentAsString();

			assertThat(svg)
					.contains("<svg")
					.contains("CONTRIBUTED")
					.contains("PLATFORM CONTEXT");
		}
	}

	@TestConfiguration(proxyBeanMethods = false)
	@EnableConfigurationProperties({
		ExperienceContentProperties.class,
		ProfileContentProperties.class,
		SeoContentProperties.class
	})
	static class ContentConfiguration {
	}

}
