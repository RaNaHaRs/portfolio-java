package com.harshrana.portfolio.web;

import java.io.IOException;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ResumeController {

	private final Resource resume = new ClassPathResource("static/resume.pdf");

	@GetMapping("/resume")
	ResponseEntity<Resource> resume() throws IOException {
		if (!resume.isReadable()) {
			throw new IllegalStateException("The resume PDF resource is missing or unreadable.");
		}

		return ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_PDF)
				.contentLength(resume.contentLength())
				.header(
						HttpHeaders.CONTENT_DISPOSITION,
						ContentDisposition.attachment().filename("Harsh-Rana-Resume.pdf").build().toString())
				.body(resume);
	}

}
