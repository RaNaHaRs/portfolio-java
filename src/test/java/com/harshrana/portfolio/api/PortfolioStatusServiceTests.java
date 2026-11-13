package com.harshrana.portfolio.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.time.Instant;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.StandardEnvironment;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

class PortfolioStatusServiceTests {

	@Test
	void readsRuntimeBuildAndAllHttpServerRequestTimers() {
		MeterRegistry registry = new SimpleMeterRegistry();
		Timer.builder("http.server.requests").tag("uri", "/").register(registry)
				.record(2, TimeUnit.MILLISECONDS);
		Timer.builder("http.server.requests").tag("uri", "/api/v1/status").register(registry)
				.record(3, TimeUnit.MILLISECONDS);
		Timer.builder("unrelated.timer").register(registry).record(1, TimeUnit.MILLISECONDS);
		PortfolioStatusService service = new PortfolioStatusService(
				registry,
				buildProperties("abcdef1234567890"),
				new StandardEnvironment());

		PortfolioStatusService.StatusResponse status = service.status();

		assertThat(status.uptimeSeconds()).isGreaterThanOrEqualTo(0);
		assertThat(status.requestsServed()).isEqualTo(2);
		assertThat(status.jvm().usedMb()).isGreaterThanOrEqualTo(0);
		assertThat(status.jvm().maxMb()).isPositive();
		assertThat(status.build().version()).isEqualTo("0.0.1-SNAPSHOT");
		assertThat(status.build().commit()).isEqualTo("abcdef1");
		assertThat(status.build().time()).isEqualTo(Instant.parse("2026-11-13T21:00:00Z"));
	}

	@Test
	void doesNotInventACommitWhenBuildMetadataDoesNotContainOne() {
		PortfolioStatusService service = new PortfolioStatusService(
				new SimpleMeterRegistry(),
				buildProperties(null),
				new StandardEnvironment());

		assertThat(service.status().build().commit()).isNull();
	}

	@Test
	void rejectsMalformedConfiguredCommitMetadata() {
		PortfolioStatusService service = new PortfolioStatusService(
				new SimpleMeterRegistry(),
				buildProperties("not-a-commit"),
				new StandardEnvironment());

		assertThatThrownBy(service::status)
				.isInstanceOf(IllegalStateException.class)
				.hasMessageContaining("hexadecimal Git hash");
	}

	private static BuildProperties buildProperties(String commit) {
		Properties properties = new Properties();
		properties.setProperty("version", "0.0.1-SNAPSHOT");
		properties.setProperty("time", "2026-11-13T21:00:00Z");
		if (commit != null) {
			properties.setProperty("git.commit.id.abbrev", commit);
		}
		return new BuildProperties(properties);
	}

}
