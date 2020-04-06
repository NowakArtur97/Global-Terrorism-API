package com.NowakArtur97.GlobalTerrorismAPI.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.NowakArtur97.GlobalTerrorismAPI.property.SwaggerConfigurationProperties;

import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Tag;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(value = SwaggerConfigurationProperties.class)
@Import(BeanValidatorPluginsConfiguration.class)
public class SwaggerConfiguration {

	public static final String TARGET_TAG = "Target Resource";
	public static final String TARGET_TAG_DESCRIPTION = "Operations related to Targets";

	@Bean
	public Docket docket(SwaggerConfigurationProperties swaggerConfigurationProperties) {

		return new Docket(DocumentationType.SWAGGER_2).useDefaultResponseMessages(false).select()
				.apis(RequestHandlerSelectors.basePackage(swaggerConfigurationProperties.getBasePackage()))
				.paths(PathSelectors.ant(swaggerConfigurationProperties.getPathSelectors())).build()
				.apiInfo(apiDetails(swaggerConfigurationProperties)).tags(new Tag(TARGET_TAG, TARGET_TAG_DESCRIPTION));
	}

	private ApiInfo apiDetails(SwaggerConfigurationProperties swaggerConfigurationProperties) {

		Contact contact = new Contact(swaggerConfigurationProperties.getContactName(),
				swaggerConfigurationProperties.getContactUrl(), swaggerConfigurationProperties.getContactEmail());

		return new ApiInfoBuilder().version(swaggerConfigurationProperties.getVersion())
				.title(swaggerConfigurationProperties.getTitle())
				.description(swaggerConfigurationProperties.getDescription())
				.termsOfServiceUrl(swaggerConfigurationProperties.getTermsOfServiceUrl())
				.license(swaggerConfigurationProperties.getLicense())
				.licenseUrl(swaggerConfigurationProperties.getLicenseUrl()).contact(contact).build();
	}
}
