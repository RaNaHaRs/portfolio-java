package com.harshrana.portfolio;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class PageContractAssertions {

	private static final Pattern H1 = Pattern.compile("<h1\\b");

	private static final Pattern TITLE = Pattern.compile(
			"<title>[^<]+ · Harsh Rana, Java Backend Developer</title>");

	private static final Pattern PERSON_JSON_LD = Pattern.compile(
			"(?s)<script type=\"application/ld\\+json\"[^>]*>(.*?)</script>");

	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

	private PageContractAssertions() {
	}

	public static void assertPageContract(String html, String canonicalUrl) throws JsonProcessingException {
		assertThat(TITLE.matcher(html).find()).isTrue();
		assertThat(H1.matcher(html).results()).hasSize(1);
		assertThat(html)
				.contains("<link rel=\"canonical\" href=\"" + canonicalUrl + "\">")
				.contains("property=\"og:url\" content=\"" + canonicalUrl + "\"")
				.contains("property=\"og:image\" content=\"http://localhost:8080/img/og-default.svg\"")
				.contains("name=\"twitter:image\" content=\"http://localhost:8080/img/og-default.svg\"");

		Matcher structuredDataMatcher = PERSON_JSON_LD.matcher(html);
		assertThat(structuredDataMatcher.find()).isTrue();
		JsonNode person = OBJECT_MAPPER.readTree(structuredDataMatcher.group(1).trim());
		assertThat(person.path("@context").asText()).isEqualTo("https://schema.org");
		assertThat(person.path("@type").asText()).isEqualTo("Person");
		assertThat(person.path("name").asText()).isEqualTo("Harsh Rana");
		assertThat(person.path("jobTitle").asText()).isEqualTo("Java Backend Developer");
		assertThat(person.path("url").asText()).isEqualTo("http://localhost:8080");
		assertThat(person.path("sameAs").isArray()).isTrue();
	}

}
