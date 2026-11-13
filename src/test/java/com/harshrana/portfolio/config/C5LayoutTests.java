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
import org.springframework.stereotype.Controller;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@WebMvcTest(controllers = C5LayoutProbeController.class)
@Import({ SecurityConfiguration.class, CspNonceModelAdvice.class })
class C5LayoutTests {

	private static final Pattern CSP_NONCE_PATTERN = Pattern.compile("'nonce-([A-Za-z0-9_-]+)'");

	private static final Pattern INLINE_SCRIPT_NONCE_PATTERN = Pattern.compile("<script nonce=\"([A-Za-z0-9_-]+)\">");

	private static final Pattern VERSIONED_BASE_CSS_PATTERN =
			Pattern.compile("href=\"/css/base-[a-f0-9]+\\.css\"");

	@Autowired
	private MockMvc mockMvc;

	@Test
	void rendersSharedLayoutFragmentsAndNonceBoundThemeScript() throws Exception {
		var result = mockMvc.perform(get("/c5-template-probe"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		String html = result.getResponse().getContentAsString();
		String csp = result.getResponse().getHeader("Content-Security-Policy");
		Matcher cspNonceMatcher = CSP_NONCE_PATTERN.matcher(csp);
		assertThat(cspNonceMatcher.find()).isTrue();
		String cspNonce = cspNonceMatcher.group(1);
		Matcher scriptNonceMatcher = INLINE_SCRIPT_NONCE_PATTERN.matcher(html);
		assertThat(scriptNonceMatcher.find()).isTrue();
		assertThat(scriptNonceMatcher.group(1)).isEqualTo(cspNonce);

		assertThat(html)
				.contains("<title>Layout probe · Harsh Rana, Java Backend Developer</title>")
				.contains("name=\"description\" content=\"Layout description\"")
				.contains("harsh.rana")
				.contains("aria-current=\"page\">Home</a>")
				.contains("Skip to content")
				.contains("Status unavailable")
				.contains("Built with Spring Boot and Thymeleaf")
				.contains("Page content marker")
				.contains("&lt;script&gt;escaped content&lt;/script&gt;")
				.contains("View projects")
				.contains("class=\"chip\"")
				.contains("class=\"card\"")
				.contains("class=\"browser-frame\"")
				.contains("class=\"code-block\"")
				.contains("class=\"callout\"")
				.contains("status-line--success")
				.contains("class=\"section-header\"")
				.contains("data-motion-toggle")
				.contains("field-error")
				.contains("aria-live=\"polite\"");
		assertThat(VERSIONED_BASE_CSS_PATTERN.matcher(html).find()).isTrue();
	}

}

@Controller
class C5LayoutProbeController {

	@GetMapping("/c5-template-probe")
	String probe(Model model) {
		model.addAttribute("probeMessage", "<script>escaped content</script>");
		return "c5-probe";
	}

}
