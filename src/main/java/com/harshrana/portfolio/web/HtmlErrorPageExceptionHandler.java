package com.harshrana.portfolio.web;

import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.web.servlet.JakartaServletWebApplication;

import com.harshrana.portfolio.content.ProfileContentProperties;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class HtmlErrorPageExceptionHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(HtmlErrorPageExceptionHandler.class);

	private final SpringTemplateEngine templateEngine;

	private final ProfileContentProperties profile;

	private final String baseUrl;

	public HtmlErrorPageExceptionHandler(
			SpringTemplateEngine templateEngine,
			ProfileContentProperties profile,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.templateEngine = templateEngine;
		this.profile = profile;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@ExceptionHandler(Exception.class)
	ResponseEntity<?> handleError(
			Exception exception,
			HttpServletRequest request,
			HttpServletResponse response) {
		HttpStatusCode status = exception instanceof ErrorResponse errorResponse
				? errorResponse.getStatusCode()
				: HttpStatus.INTERNAL_SERVER_ERROR;
		String exceptionFile;
		String message;
		String pageTitle;
		String pageDescription;

		if (status.value() == HttpStatus.NOT_FOUND.value()) {
			exceptionFile = "NotFoundException.java";
			message = "404: this route isn’t mapped.";
			pageTitle = "Page not found";
			pageDescription = "The requested page could not be found.";
		}
		else if (status.is5xxServerError()) {
			exceptionFile = "ServerErrorException.java";
			message = status.value() + ": something went wrong on the server.";
			pageTitle = "Server error";
			pageDescription = "An unexpected error occurred. Please try again later.";
			LOGGER.error("Unhandled exception while processing an error response", exception);
		}
		else {
			exceptionFile = "ClientErrorException.java";
			message = status.value() + ": this request could not be completed.";
			pageTitle = "Request error";
			pageDescription = "The request could not be completed.";
		}

		if (isApiRequest(request)) {
			ProblemDetail problem = ProblemDetail.forStatusAndDetail(status, detailFor(status));
			HttpStatus knownStatus = HttpStatus.resolve(status.value());
			problem.setTitle(knownStatus == null ? "Error" : knownStatus.getReasonPhrase());
			problem.setInstance(URI.create(request.getRequestURI()));
			return ResponseEntity.status(status)
					.contentType(MediaType.APPLICATION_PROBLEM_JSON)
					.body(problem);
		}

		Object nonce = request.getAttribute("cspNonce");
		if (!(nonce instanceof String nonceValue)) {
			throw new IllegalStateException("The CSP nonce is not available for this request.");
		}
		Map<String, Object> model = Map.of(
				"status", status.value(),
				"exceptionFile", exceptionFile,
				"message", message,
				"pageTitle", pageTitle,
				"pageDescription", pageDescription,
				"cspNonce", nonceValue,
				"canonicalUrl", baseUrl + request.getRequestURI(),
				"openGraphImageUrl", baseUrl + "/img/og-default.svg",
				"personJsonLd", new SeoPersonModelAdvice.PersonJsonLd(
						profile.name(),
						profile.role(),
						baseUrl,
						List.of(profile.links().github(), profile.links().linkedIn())));
		WebContext context = new WebContext(
				JakartaServletWebApplication.buildApplication(request.getServletContext())
						.buildExchange(request, response),
				request.getLocale(),
				model);
		String html = templateEngine.process("fragments/error-page", context);
		return ResponseEntity.status(status)
				.contentType(new MediaType(MediaType.TEXT_HTML, StandardCharsets.UTF_8))
				.body(html);
	}

	private static boolean isApiRequest(HttpServletRequest request) {
		String requestUri = request.getRequestURI();
		return requestUri.equals("/api") || requestUri.startsWith("/api/");
	}

	private static String detailFor(HttpStatusCode status) {
		if (status.value() == HttpStatus.NOT_FOUND.value()) {
			return "The requested resource was not found.";
		}
		return status.is5xxServerError()
				? "An unexpected error occurred."
				: "The request could not be completed.";
	}

}
