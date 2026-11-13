package com.harshrana.portfolio.content;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties("portfolio.content.about")
public record AboutContentProperties(@NotEmpty List<@NotBlank String> story) {
}
