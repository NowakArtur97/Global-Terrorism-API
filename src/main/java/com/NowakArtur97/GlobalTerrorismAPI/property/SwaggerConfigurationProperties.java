package com.NowakArtur97.GlobalTerrorismAPI.property;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties(prefix = "swagger")
@ConstructorBinding
@Getter
@AllArgsConstructor
public final class SwaggerConfigurationProperties {

    private final String version;

    private final String title;

    private final String description;

    private final String termsOfServiceUrl;

    private final String license;

    private final String licenseUrl;

    private final String contactName;

    private final String contactEmail;

    private final String contactUrl;

    private final String pathSelectors;

    private final String authorizationHeader;
}
