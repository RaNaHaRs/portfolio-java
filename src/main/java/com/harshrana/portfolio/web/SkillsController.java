package com.harshrana.portfolio.web;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.harshrana.portfolio.content.SeoContentProperties;
import com.harshrana.portfolio.content.SkillsContentProperties;
import com.harshrana.portfolio.web.ProjectContentService.ProjectPage;

@Controller
public class SkillsController {

	private final ProjectContentService projects;

	private final SeoContentProperties seo;

	private final SkillsContentProperties skills;

	private final String baseUrl;

	public SkillsController(
			ProjectContentService projects,
			SeoContentProperties seo,
			SkillsContentProperties skills,
			@Value("${portfolio.site.base-url}") String baseUrl) {
		this.projects = projects;
		this.seo = seo;
		this.skills = skills;
		this.baseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
	}

	@GetMapping("/skills")
	String skills(Model model) {
		SeoContentProperties.Page metadata = seo.pages().stream()
				.filter(page -> page.route().equals("/skills"))
				.map(SeoContentProperties.PageEntry::metadata)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("Missing SEO metadata for route /skills."));
		List<ProjectPage> visibleProjects = projects.visibleProjects();
		Map<String, List<Skill>> skillsByLevel = new LinkedHashMap<>();
		for (SkillsContentProperties.Category category : skills.categories()) {
			for (SkillsContentProperties.Skill skill : category.skills()) {
				List<ProjectReference> projectReferences = visibleProjects.stream()
						.filter(project -> skill.usedIn() != null
								&& skill.usedIn().contains(project.project().slug()))
						.map(project -> new ProjectReference(project.project().slug(), project.project().title()))
						.toList();
				String level = skill.level() == null || skill.level().isBlank()
						? "Level not specified"
						: skills.levelsConfirmed() ? skill.level() : "Proposed: " + skill.level();
				skillsByLevel.computeIfAbsent(level, ignored -> new ArrayList<>())
						.add(new Skill(category.name(), skill.name(), projectReferences));
			}
		}
		List<SkillGroup> skillGroups = skillsByLevel.entrySet().stream()
				.map(entry -> new SkillGroup(entry.getKey(), entry.getValue()))
				.sorted(Comparator.comparingInt(group -> levelOrder(group.name())))
				.toList();

		model.addAttribute("pageTitle", metadata.title());
		model.addAttribute("pageDescription", metadata.description());
		model.addAttribute("canonicalUrl", baseUrl + "/skills");
		model.addAttribute("openGraphImageUrl", metadata.openGraphImage());
		model.addAttribute("skillGroups", skillGroups);
		model.addAttribute("levelsConfirmed", skills.levelsConfirmed());
		return "pages/skills";
	}

	private int levelOrder(String name) {
		if (name.endsWith("Strong")) {
			return 0;
		}
		if (name.endsWith("Working knowledge")) {
			return 1;
		}
		if (name.endsWith("Learning")) {
			return 2;
		}
		if (name.equals("Level not specified")) {
			return 3;
		}
		return 4;
	}

	public record SkillGroup(String name, List<Skill> skills) {
	}

	public record Skill(String category, String name, List<ProjectReference> projects) {
	}

	public record ProjectReference(String slug, String title) {
	}

}
