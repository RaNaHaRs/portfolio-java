package com.harshrana.portfolio.config;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class SecurityHeadersFilterTests {

	private static final Pattern CSP_NONCE_PATTERN = Pattern.compile("'nonce-([A-Za-z0-9_-]+)'");

	private final SecurityHeadersFilter filter = new SecurityHeadersFilter();

	@Test
	void addsSecurityHeadersAndRequestScopedCspNonce() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		String[] nonceSeenByChain = new String[1];

		filter.doFilter(request, response, (servletRequest, servletResponse) -> nonceSeenByChain[0] =
				(String) request.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE));

		String csp = response.getHeader("Content-Security-Policy");
		Matcher nonceMatcher = CSP_NONCE_PATTERN.matcher(csp);
		assertThat(nonceMatcher.find()).isTrue();
		String nonce = nonceMatcher.group(1);

		assertThat(request.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE)).isEqualTo(nonce);
		assertThat(nonceSeenByChain[0]).isEqualTo(nonce);
		assertThat(CSP_NONCE_PATTERN.matcher(csp).results().map(match -> match.group(1)).toList())
				.containsExactly(nonce, nonce);
		assertThat(csp)
				.contains("default-src 'self'")
				.contains("font-src 'self'")
				.contains("connect-src 'self'")
				.contains("img-src 'self' data: https://github.com")
				.contains("frame-ancestors 'none'");
		assertThat(response.getHeader("X-Content-Type-Options")).isEqualTo("nosniff");
		assertThat(response.getHeader("Referrer-Policy")).isEqualTo("strict-origin-when-cross-origin");
		assertThat(response.getHeader("Permissions-Policy")).contains("camera=()", "geolocation=()");
	}

	@Test
	void generatesDifferentNonceForEachRequest() throws ServletException, IOException {
		MockHttpServletRequest firstRequest = new MockHttpServletRequest();
		MockHttpServletRequest secondRequest = new MockHttpServletRequest();
		MockHttpServletResponse firstResponse = new MockHttpServletResponse();
		MockHttpServletResponse secondResponse = new MockHttpServletResponse();

		filter.doFilter(firstRequest, firstResponse, (request, response) -> {
		});
		filter.doFilter(secondRequest, secondResponse, (request, response) -> {
		});

		assertThat(firstRequest.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE))
				.isNotEqualTo(secondRequest.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE));
	}

}
