package com.NowakArtur97.GlobalTerrorismAPI.property;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties
@Data
public class SwaggerConfigurationProperties {

	@Value("${swagger.api.version}")
	private String apiVersion;

	@Value("${swagger.title}")
	private String title;

	@Value("${swagger.description}")
	private String description;

	@Value("${swagger.termsOfServiceUrl}")
	private String termsOfServiceUrl;

	@Value("${swagger.license}")
	private String license;

	@Value("${swagger.licenseUrl}")
	private String licenseUrl;

	@Value("${swagger.contact.name}")
	private String contactName;

	@Value("${swagger.contact.email}")
	private String contactEmail;

	@Value("${swagger.contact.url}")
	private String contactUrl;

	@Value("${swagger.basePackage}")
	private String basePackage;

	@Value("${swagger.pathSelectors}")
	private String pathSelectors;
}
