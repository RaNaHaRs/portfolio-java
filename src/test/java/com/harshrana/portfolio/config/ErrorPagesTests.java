package com.harshrana.portfolio.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;

import com.harshrana.portfolio.web.HtmlErrorPageExceptionHandler;

@WebMvcTest(controllers = ErrorPageProbeController.class)
@Import({ SecurityConfiguration.class, CspNonceModelAdvice.class, HtmlErrorPageExceptionHandler.class })
class ErrorPagesTests {

	private static final Pattern CSP_NONCE_PATTERN = Pattern.compile("'nonce-([A-Za-z0-9_-]+)'");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersStyledNotFoundPageWithNotFoundStatus() throws Exception {
		var result = mockMvc.perform(get("/missing-page").accept(MediaType.TEXT_HTML))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn();

		String html = result.getResponse().getContentAsString();
		String csp = result.getResponse().getHeader("Content-Security-Policy");
		Matcher cspNonceMatcher = CSP_NONCE_PATTERN.matcher(csp);
		assertThat(cspNonceMatcher.find()).isTrue();
		assertThat(html)
				.contains("<html lang=\"en\"")
				.contains("<title>Page not found · Harsh Rana, Java Backend Developer</title>")
				.contains("nonce=\"" + cspNonceMatcher.group(1) + "\"")
				.contains("NotFoundException.java")
				.contains("404: this route isn’t mapped.")
				.contains("Go home")
				.contains("href=\"/projects\"");
	}

	@Test
	void rendersGenericServerErrorPageWithoutExposingExceptionDetails() throws Exception {
		var result = mockMvc.perform(get("/test/server-error").accept(MediaType.TEXT_HTML))
				.andExpect(status().isInternalServerError())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn();

		assertThat(result.getResponse().getContentAsString())
				.contains("ServerErrorException.java")
				.contains("500: something went wrong on the server.")
				.doesNotContain("sensitive internal details");
	}

	@Test
	void preservesClientErrorStatusAndDoesNotExposeItsMessage() throws Exception {
		var result = mockMvc.perform(get("/test/client-error").accept(MediaType.TEXT_HTML))
				.andExpect(status().isBadRequest())
				.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
				.andReturn();

		assertThat(result.getResponse().getContentAsString())
				.contains("400: this request could not be completed.")
				.doesNotContain("sensitive internal details");
	}

	@Test
	void keepsApiErrorsAsProblemDetails() throws Exception {
		mockMvc.perform(get("/api/v1/missing")
				.accept(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON))
				.andExpect(content().json("""
						{"status":404,"title":"Not Found","instance":"/api/v1/missing"}
						"""));
	}

	@Test
	void keepsUnspecifiedApiAcceptAsProblemDetails() throws Exception {
		mockMvc.perform(get("/api/v1/missing"))
				.andExpect(status().isNotFound())
				.andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_PROBLEM_JSON));
	}

}

@Controller
class ErrorPageProbeController {

	@GetMapping("/test/server-error")
	void serverError() {
		throw new IllegalStateException("sensitive internal details");
	}

	@GetMapping("/test/client-error")
	void clientError() {
		throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "sensitive internal details");
	}

}
