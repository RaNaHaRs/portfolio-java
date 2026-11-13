package com.harshrana.portfolio.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = SecurityProbeController.class)
@Import(SecurityConfiguration.class)
@ActiveProfiles("prod")
class ProductionSecurityConfigurationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void sendsHstsOnlyForSecureRequestsInProduction() throws Exception {
		mockMvc.perform(get("/").with(request -> {
			request.setSecure(true);
			return request;
		}))
				.andExpect(status().isOk())
				.andExpect(result -> org.assertj.core.api.Assertions.assertThat(
						result.getResponse().getHeader("Strict-Transport-Security"))
						.contains("max-age=31536000")
						.contains("includeSubDomains"));

		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(header().doesNotExist("Strict-Transport-Security"));
	}

}
