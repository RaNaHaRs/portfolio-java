package com.harshrana.portfolio.web;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.commonmark.node.Code;
import org.commonmark.node.Document;
import org.commonmark.node.Heading;
import org.commonmark.node.HtmlInline;
import org.commonmark.node.Node;
import org.commonmark.node.Text;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

final class CaseStudyMarkdownRenderer {

	private final Parser parser = Parser.builder().build();

	RenderedCaseStudy render(String markdown, String pageTitle) {
		Document document = (Document) parser.parse(markdown);
		Node first = document.getFirstChild();
		if (first instanceof Heading heading
				&& heading.getLevel() == 1
				&& headingText(heading).trim().equalsIgnoreCase(pageTitle)) {
			heading.unlink();
		}
		List<Heading> headings = new ArrayList<>();
		document.accept(new org.commonmark.node.AbstractVisitor() {
			@Override
			public void visit(Heading heading) {
				if (heading.getLevel() == 1) {
					heading.setLevel(2);
				}
				headings.add(heading);
				visitChildren(heading);
			}
		});

		Map<Heading, String> headingIds = new HashMap<>();
		Set<String> usedIds = new HashSet<>();
		List<ProjectContentService.TocHeading> tableOfContents = new ArrayList<>();
		for (Heading heading : headings) {
			String label = headingText(heading).trim();
			if (label.isEmpty()) {
				continue;
			}
			String baseId = headingId(label);
			String id = baseId;
			for (int suffix = 2; !usedIds.add(id); suffix++) {
				id = baseId + "-" + suffix;
			}
			headingIds.put(heading, id);
			tableOfContents.add(new ProjectContentService.TocHeading(label, id, heading.getLevel()));
		}

		HtmlRenderer renderer = HtmlRenderer.builder()
				.escapeHtml(true)
				.sanitizeUrls(true)
				.attributeProviderFactory(context -> (node, tagName, attributes) -> {
					if (node instanceof Heading heading && headingIds.containsKey(heading)) {
						attributes.put("id", headingIds.get(heading));
					}
				})
				.build();
		return new RenderedCaseStudy(renderer.render(document), List.copyOf(tableOfContents));
	}

	private static String headingText(Node node) {
		StringBuilder text = new StringBuilder();
		appendText(node, text);
		return text.toString();
	}

	private static void appendText(Node node, StringBuilder text) {
		if (node instanceof Text value) {
			text.append(value.getLiteral());
			return;
		}
		if (node instanceof Code value) {
			text.append(value.getLiteral());
			return;
		}
		if (node instanceof HtmlInline value) {
			text.append(value.getLiteral());
			return;
		}
		for (Node child = node.getFirstChild(); child != null; child = child.getNext()) {
			appendText(child, text);
		}
	}

	private static String headingId(String label) {
		String normalized = Normalizer.normalize(label, Normalizer.Form.NFD)
				.replaceAll("\\p{M}+", "")
				.toLowerCase(Locale.ROOT)
				.replaceAll("[^a-z0-9]+", "-")
				.replaceAll("^-|-$", "");
		return normalized.isEmpty() ? "section" : normalized;
	}

	record RenderedCaseStudy(String html, List<ProjectContentService.TocHeading> tableOfContents) {
	}

}
