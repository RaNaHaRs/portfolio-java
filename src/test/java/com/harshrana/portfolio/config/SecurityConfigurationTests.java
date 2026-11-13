package com.harshrana.portfolio.config;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@WebMvcTest(controllers = SecurityProbeController.class)
@Import(SecurityConfiguration.class)
class SecurityConfigurationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void publicRoutesReceiveSecurityHeadersWithoutADevelopmentHstsHeader() throws Exception {
		mockMvc.perform(get("/"))
				.andExpect(status().isOk())
				.andExpect(header().exists("Content-Security-Policy"))
				.andExpect(header().string("X-Content-Type-Options", "nosniff"))
				.andExpect(header().string("Referrer-Policy", "strict-origin-when-cross-origin"))
				.andExpect(header().exists("Permissions-Policy"))
				.andExpect(header().string("X-Frame-Options", "DENY"))
				.andExpect(header().doesNotExist("Strict-Transport-Security"));

		mockMvc.perform(get("/actuator/health"))
				.andExpect(status().isOk());
	}

	@Test
	void deniesOtherActuatorRoutes() throws Exception {
		mockMvc.perform(get("/actuator/private"))
				.andExpect(status().isForbidden());
	}

	@Test
	void requiresCsrfForStateChangingRequests() throws Exception {
		mockMvc.perform(post("/contact"))
				.andExpect(status().isForbidden());

		mockMvc.perform(post("/contact").with(csrf()))
				.andExpect(status().isNoContent());
	}

}

@RestController
class SecurityProbeController {

	@GetMapping({ "/", "/actuator/health" })
	String publicRoute() {
		return "public";
	}

	@PostMapping("/contact")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	void postContact() {
	}

}
