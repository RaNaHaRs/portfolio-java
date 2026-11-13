package com.harshrana.portfolio.api;

import java.lang.management.ManagementFactory;
import java.time.Instant;
import java.util.Locale;
import java.util.regex.Pattern;

import org.springframework.boot.info.BuildProperties;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Service
public class PortfolioStatusService {

	private static final Pattern GIT_COMMIT = Pattern.compile("[0-9a-fA-F]{7,40}");

	private static final long BYTES_PER_MEGABYTE = 1024L * 1024L;

	private final MeterRegistry meterRegistry;

	private final BuildProperties buildProperties;

	private final Environment environment;

	public PortfolioStatusService(
			MeterRegistry meterRegistry,
			BuildProperties buildProperties,
			Environment environment) {
		this.meterRegistry = meterRegistry;
		this.buildProperties = buildProperties;
		this.environment = environment;
	}

	public StatusResponse status() {
		Runtime runtime = Runtime.getRuntime();
		long usedBytes = runtime.totalMemory() - runtime.freeMemory();
		return new StatusResponse(
				ManagementFactory.getRuntimeMXBean().getUptime() / 1000,
				requestsServed(),
				new JvmStatus(usedBytes / BYTES_PER_MEGABYTE, runtime.maxMemory() / BYTES_PER_MEGABYTE),
				new BuildStatus(buildProperties.getVersion(), buildCommit(), buildProperties.getTime()));
	}

	private long requestsServed() {
		return meterRegistry.getMeters().stream()
				.filter(meter -> meter instanceof Timer)
				.filter(meter -> meter.getId().getName().equals("http.server.requests"))
				.map(Timer.class::cast)
				.mapToLong(Timer::count)
				.sum();
	}

	private String buildCommit() {
		String commit = firstNonBlank(
				buildProperties.get("git.commit.id.abbrev"),
				buildProperties.get("git.commit.id"),
				environment.getProperty("GIT_COMMIT"),
				environment.getProperty("GITHUB_SHA"));
		if (commit == null) {
			return null;
		}

		String normalizedCommit = commit.trim();
		if (!GIT_COMMIT.matcher(normalizedCommit).matches()) {
			throw new IllegalStateException("The configured build commit must be a hexadecimal Git hash.");
		}
		return normalizedCommit.substring(0, Math.min(7, normalizedCommit.length()))
				.toLowerCase(Locale.ROOT);
	}

	private static String firstNonBlank(String... values) {
		for (String value : values) {
			if (value != null && !value.isBlank()) {
				return value;
			}
		}
		return null;
	}

	public record StatusResponse(
			long uptimeSeconds,
			long requestsServed,
			JvmStatus jvm,
			BuildStatus build) {
	}

	public record JvmStatus(long usedMb, long maxMb) {
	}

	public record BuildStatus(String version, String commit, Instant time) {
	}

}
