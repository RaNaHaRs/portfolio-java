package com.harshrana.portfolio.config;

import java.io.IOException;
import java.security.SecureRandom;
import java.util.Base64;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;

final class SecurityHeadersFilter extends OncePerRequestFilter {

	static final String CSP_NONCE_ATTRIBUTE = "cspNonce";

	private static final SecureRandom SECURE_RANDOM = new SecureRandom();

	private static final String CSP_POLICY = "default-src 'self'; "
			+ "base-uri 'self'; "
			+ "object-src 'none'; "
			+ "frame-ancestors 'none'; "
			+ "form-action 'self'; "
			+ "script-src 'self' 'nonce-%s'; "
			+ "style-src 'self' 'nonce-%s'; "
			+ "font-src 'self'; "
			+ "connect-src 'self'; "
			+ "img-src 'self' data: https://github.com";

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		byte[] nonceBytes = new byte[18];
		SECURE_RANDOM.nextBytes(nonceBytes);
		String nonce = Base64.getUrlEncoder().withoutPadding().encodeToString(nonceBytes);

		request.setAttribute(CSP_NONCE_ATTRIBUTE, nonce);
		response.setHeader("Content-Security-Policy", CSP_POLICY.formatted(nonce, nonce));
		response.setHeader("X-Content-Type-Options", "nosniff");
		response.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
		response.setHeader("Permissions-Policy",
				"accelerometer=(), camera=(), geolocation=(), gyroscope=(), microphone=(), payment=(), usb=()");

		filterChain.doFilter(request, response);
	}

}
