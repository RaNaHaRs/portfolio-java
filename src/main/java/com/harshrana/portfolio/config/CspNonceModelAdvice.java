package com.harshrana.portfolio.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CspNonceModelAdvice {

	@ModelAttribute
	void addCspNonce(HttpServletRequest request, Model model) {
		Object nonce = request.getAttribute(SecurityHeadersFilter.CSP_NONCE_ATTRIBUTE);
		if (!(nonce instanceof String nonceValue)) {
			throw new IllegalStateException("The CSP nonce is not available for this request.");
		}
		model.addAttribute("cspNonce", nonceValue);
	}

}
