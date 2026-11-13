package com.harshrana.portfolio.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.header.HeaderWriterFilter;

@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http, Environment environment) throws Exception {
		boolean production = environment.acceptsProfiles(Profiles.of("prod", "production"));

		http
				.csrf(Customizer.withDefaults())
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
						.requestMatchers("/actuator", "/actuator/**").denyAll()
						.anyRequest().permitAll())
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.headers(headers -> {
					headers.frameOptions(frameOptions -> frameOptions.deny());
					headers.httpStrictTransportSecurity(hsts -> {
						if (production) {
							hsts.maxAgeInSeconds(31_536_000).includeSubDomains(true);
						}
						else {
							hsts.disable();
						}
					});
				})
				.addFilterBefore(new SecurityHeadersFilter(), HeaderWriterFilter.class);

		return http.build();
	}

}
