package com.harshrana.portfolio.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.bind.validation.ValidationBindHandler;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;

class ContentPropertiesTests {

	private static final LocalValidatorFactoryBean VALIDATOR = new LocalValidatorFactoryBean();

	@BeforeAll
	static void initializeValidator() {
		VALIDATOR.afterPropertiesSet();
	}

	@AfterAll
	static void closeValidatorFactory() {
		VALIDATOR.close();
	}

	@Test
	void bindsAndValidatesAllContentFiles() throws IOException {
		StandardEnvironment environment = contentEnvironment();
		Binder binder = Binder.get(environment);
		ValidationBindHandler validation = new ValidationBindHandler(VALIDATOR);

		ProfileContentProperties profile = binder.bind("portfolio.content.profile",
				Bindable.of(ProfileContentProperties.class), validation).get();
		AboutContentProperties about = binder.bind("portfolio.content.about",
				Bindable.of(AboutContentProperties.class), validation).get();
		HomeContentProperties home = binder.bind("portfolio.content.home",
				Bindable.of(HomeContentProperties.class), validation).get();
		ProjectsContentProperties projects = binder.bind("portfolio.content.projects",
				Bindable.of(ProjectsContentProperties.class), validation).get();
		ExperienceContentProperties experience = binder.bind("portfolio.content.experience",
				Bindable.of(ExperienceContentProperties.class), validation).get();
		SkillsContentProperties skills = binder.bind("portfolio.content.skills",
				Bindable.of(SkillsContentProperties.class), validation).get();
		TimelineContentProperties timeline = binder.bind("portfolio.content.timeline",
				Bindable.of(TimelineContentProperties.class), validation).get();
		CertificationsContentProperties certifications = binder.bind("portfolio.content.certifications",
				Bindable.of(CertificationsContentProperties.class), validation).get();
		SeoContentProperties seo = binder.bind("portfolio.content.seo",
				Bindable.of(SeoContentProperties.class), validation).get();

		assertThat(profile.name()).isEqualTo("Harsh Rana");
		assertThat(profile.email()).isNull();
		assertThat(profile.flags().showCgpa()).isFalse();
		assertThat(profile.flags().contact().form().enabled()).isTrue();
		assertThat(about.story()).hasSize(3);
		assertThat(home.proofItems()).hasSize(4);
		assertThat(home.journeyStops()).hasSize(5);
		assertThat(projects.showInProgress()).isFalse();
		assertThat(projects.items()).hasSize(3);
		assertThat(projects.items()).allSatisfy(project -> assertThat(
				new java.io.File("src/main/resources/content").toPath().resolve(project.caseStudy()).toFile())
				.exists());
		assertThat(projects.items().get(1).typeNote()).contains("not a solo project");
		assertThat(experience.positions()).hasSize(1);
		assertThat(skills.levelsConfirmed()).isFalse();
		assertThat(timeline.events()).isNotEmpty();
		assertThat(timeline.events()).filteredOn(TimelineContentProperties.Event::approvedForDisplay)
				.extracting(TimelineContentProperties.Event::title)
				.doesNotContain("MovieSearch Recommendation Engine", "Hyperlink InfoSystem");
		assertThat(certifications.items()).allSatisfy(certification ->
				assertThat(certification.isPublic()).isFalse());
		assertThat(seo.pages()).hasSize(6);
	}

	@Test
	void rejectsMissingRequiredProfileFields() throws IOException {
		StandardEnvironment environment = contentEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource("invalid-profile",
				Map.of("portfolio.content.profile.name", "")));
		Binder binder = Binder.get(environment);

		assertThatThrownBy(() -> binder.bind("portfolio.content.profile",
				Bindable.of(ProfileContentProperties.class), new ValidationBindHandler(VALIDATOR)).get())
				.isInstanceOfSatisfying(BindException.class, exception ->
						assertThat(exception.getCause()).hasMessageContaining("field 'name'"));
	}

	private static StandardEnvironment contentEnvironment() throws IOException {
		StandardEnvironment environment = new StandardEnvironment();
		environment.getPropertySources().addFirst(new MapPropertySource("test-environment",
				Map.of(
						"CONTACT_FORM_ENABLED", "true",
						"portfolio.site.base-url", "http://localhost:8080")));

		List<String> files = List.of(
				"profile.yml",
				"about.yml",
				"home.yml",
				"projects.yml",
				"experience.yml",
				"skills.yml",
				"timeline.yml",
				"certifications.yml",
				"seo.yml");
		for (String file : files) {
			ClassPathResource resource = new ClassPathResource("content/" + file);
			YamlPropertiesFactoryBean yaml = new YamlPropertiesFactoryBean();
			yaml.setResources(resource);
			environment.getPropertySources().addLast(
					new PropertiesPropertySource(file, yaml.getObject()));
		}
		return environment;
	}

}
