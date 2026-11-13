package com.harshrana.portfolio.api;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.harshrana.portfolio.content.ExperienceContentProperties;
import com.harshrana.portfolio.content.ProfileContentProperties;
import com.harshrana.portfolio.content.ProjectsContentProperties;
import com.harshrana.portfolio.content.SkillsContentProperties;
import com.harshrana.portfolio.web.ProjectContentService;
import com.harshrana.portfolio.web.ProjectContentService.ProjectPage;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Portfolio API", description = "Read-only portfolio content and runtime status.")
public class PortfolioApiController {

	private final ExperienceContentProperties experience;

	private final ProfileContentProperties profile;

	private final ProjectContentService projects;

	private final SkillsContentProperties skills;

	private final PortfolioStatusService statusService;

	public PortfolioApiController(
			ExperienceContentProperties experience,
			ProfileContentProperties profile,
			ProjectContentService projects,
			SkillsContentProperties skills,
			PortfolioStatusService statusService) {
		this.experience = experience;
		this.profile = profile;
		this.projects = projects;
		this.skills = skills;
		this.statusService = statusService;
	}

	@GetMapping("/profile")
	@Operation(summary = "Get public profile details")
	@ApiResponse(responseCode = "200", description = "Public profile content.")
	public ProfileResponse profile() {
		ProfileContentProperties.Education education = profile.education();
		return new ProfileResponse(
				profile.name(),
				profile.role(),
				profile.tagline(),
				profile.statement(),
				profile.location(),
				profile.email(),
				profile.links(),
				profile.resumePath(),
				profile.photo(),
				new EducationResponse(
						education.degree(),
						education.field(),
						education.institution(),
						education.university(),
						education.graduation().expectedYear(),
						profile.flags().showCgpa() ? education.cgpa() : null),
				profile.careerGoal(),
				profile.motto());
	}

	@GetMapping("/projects")
	@Operation(summary = "List visible projects in content order")
	@ApiResponse(responseCode = "200", description = "Visible project summaries.")
	public List<ProjectResponse> projects() {
		return projects.visibleProjects().stream()
				.map(ProjectPage::project)
				.map(PortfolioApiController::projectResponse)
				.toList();
	}

	@GetMapping("/projects/{slug}")
	@Operation(summary = "Get a visible project and its rendered case study")
	@ApiResponses({
		@ApiResponse(responseCode = "200", description = "Project details."),
		@ApiResponse(responseCode = "404", description = "The project is unknown or not visible.")
	})
	public ProjectDetailResponse project(
			@Parameter(description = "Project slug", example = "rentify")
			@PathVariable String slug) {
		ProjectPage projectPage = projects.findVisible(slug)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
		return new ProjectDetailResponse(
				projectResponse(projectPage.project()),
				projectPage.caseStudyHtml(),
				projectPage.tableOfContents());
	}

	@GetMapping("/skills")
	@Operation(summary = "List skills and their visible project evidence")
	@ApiResponse(responseCode = "200", description = "Skill categories and project associations.")
	public SkillsResponse skills() {
		List<ProjectContentService.ProjectPage> visibleProjects = projects.visibleProjects();
		List<SkillCategoryResponse> categories = skills.categories().stream()
				.map(category -> new SkillCategoryResponse(category.name(), category.skills().stream()
						.map(skill -> new SkillResponse(
								skill.name(),
								skill.level(),
								visibleProjects.stream()
										.filter(project -> skill.usedIn() != null
												&& skill.usedIn().contains(project.project().slug()))
										.map(project -> new ProjectReference(
												project.project().slug(), project.project().title()))
										.toList()))
						.toList()))
				.toList();
		return new SkillsResponse(skills.levelsConfirmed(), categories);
	}

	@GetMapping("/experience")
	@Operation(summary = "List public experience and responsibilities")
	@ApiResponse(responseCode = "200", description = "Experience content.")
	public List<ExperienceResponse> experience() {
		return experience.positions().stream()
				.map(position -> new ExperienceResponse(
						position.role(),
						position.organization(),
						position.location(),
						position.start(),
						position.end(),
						position.summary(),
						position.stack(),
						position.responsibilities(),
						position.recognitions() == null ? List.of() : position.recognitions().stream()
								.filter(ExperienceContentProperties.Recognition::approvedForPublicDisplay)
								.map(ExperienceContentProperties.Recognition::title)
								.toList()))
				.toList();
	}

	@GetMapping("/status")
	@Operation(summary = "Get live JVM, request, and build status")
	@ApiResponse(responseCode = "200", description = "Current process and build status.")
	public PortfolioStatusService.StatusResponse status() {
		return statusService.status();
	}

	private static ProjectResponse projectResponse(ProjectsContentProperties.Project project) {
		return new ProjectResponse(
				project.slug(),
				project.order(),
				project.status(),
				project.title(),
				project.category(),
				project.typeNote(),
				project.summary(),
				project.stack(),
				project.links(),
				project.screenshots(),
				project.highlights(),
				project.apiSurface(),
				project.architectureDiagram(),
				project.erDiagram());
	}

	public record ProfileResponse(
			String name,
			String role,
			String tagline,
			String statement,
			String location,
			String email,
			ProfileContentProperties.Links links,
			String resumePath,
			String photo,
			EducationResponse education,
			String careerGoal,
			String motto) {
	}

	public record EducationResponse(
			String degree,
			String field,
			String institution,
			String university,
			Integer expectedGraduationYear,
			java.math.BigDecimal cgpa) {
	}

	public record ProjectResponse(
			String slug,
			int order,
			ProjectsContentProperties.Status status,
			String title,
			String category,
			String typeNote,
			String summary,
			List<String> stack,
			ProjectsContentProperties.Links links,
			List<ProjectsContentProperties.Screenshot> screenshots,
			List<String> highlights,
			List<ProjectsContentProperties.ApiEndpoint> apiSurface,
			String architectureDiagram,
			String erDiagram) {
	}

	public record ProjectDetailResponse(
			ProjectResponse project,
			String caseStudyHtml,
			List<ProjectContentService.TocHeading> tableOfContents) {
	}

	public record SkillsResponse(boolean levelsConfirmed, List<SkillCategoryResponse> categories) {
	}

	public record SkillCategoryResponse(String name, List<SkillResponse> skills) {
	}

	public record SkillResponse(String name, String level, List<ProjectReference> usedIn) {
	}

	public record ProjectReference(String slug, String title) {
	}

	public record ExperienceResponse(
			String role,
			String organization,
			String location,
			String start,
			String end,
			String summary,
			List<String> stack,
			List<String> responsibilities,
			List<String> recognitions) {
	}

}
