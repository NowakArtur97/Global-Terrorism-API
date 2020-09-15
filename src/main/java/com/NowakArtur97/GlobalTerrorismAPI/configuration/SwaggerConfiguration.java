package com.NowakArtur97.GlobalTerrorismAPI.configuration;

import com.NowakArtur97.GlobalTerrorismAPI.feature.city.CityTag;
import com.NowakArtur97.GlobalTerrorismAPI.feature.country.CountryTag;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventTag;
import com.NowakArtur97.GlobalTerrorismAPI.feature.event.EventTargetTag;
import com.NowakArtur97.GlobalTerrorismAPI.feature.province.ProvinceTag;
import com.NowakArtur97.GlobalTerrorismAPI.feature.region.RegionTag;
import com.NowakArtur97.GlobalTerrorismAPI.feature.target.TargetTag;
import com.NowakArtur97.GlobalTerrorismAPI.property.SwaggerConfigurationProperties;
import com.NowakArtur97.GlobalTerrorismAPI.tag.*;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.List;

@Configuration
@EnableSwagger2
@EnableConfigurationProperties(value = SwaggerConfigurationProperties.class)
@Import({BeanValidatorPluginsConfiguration.class, BulkApiConfiguration.class})
class SwaggerConfiguration {

    @Bean
    Docket docket(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.ant(swaggerConfigurationProperties.getPathSelectors()))
                .build()
                .apiInfo(getApiDetails(swaggerConfigurationProperties))
                .tags(new Tag(TargetTag.RESOURCE, TargetTag.DESCRIPTION),
                        new Tag(EventTag.RESOURCE, EventTag.DESCRIPTION),
                        new Tag(GroupTag.RESOURCE, GroupTag.DESCRIPTION),
                        new Tag(GroupEventsTag.RESOURCE, GroupEventsTag.DESCRIPTION),
                        new Tag(EventTargetTag.RESOURCE, EventTargetTag.DESCRIPTION),
                        new Tag(CityTag.RESOURCE, CityTag.DESCRIPTION),
                        new Tag(ProvinceTag.RESOURCE, ProvinceTag.DESCRIPTION),
                        new Tag(CountryTag.RESOURCE, CountryTag.DESCRIPTION),
                        new Tag(RegionTag.RESOURCE, RegionTag.DESCRIPTION),
                        new Tag(UserRegistrationTag.RESOURCE, UserRegistrationTag.DESCRIPTION),
                        new Tag(AuthenticationTag.RESOURCE, AuthenticationTag.DESCRIPTION)
                )
                .securityContexts(List.of(getSecurityContext(swaggerConfigurationProperties)))
                .securitySchemes(List.of(getApiKey(swaggerConfigurationProperties)));
    }

    private ApiInfo getApiDetails(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        Contact contact = new Contact(swaggerConfigurationProperties.getContactName(),
                swaggerConfigurationProperties.getContactUrl(), swaggerConfigurationProperties.getContactEmail());

        return new ApiInfoBuilder()
                .version(swaggerConfigurationProperties.getVersion())
                .title(swaggerConfigurationProperties.getTitle())
                .description(swaggerConfigurationProperties.getDescription())
                .termsOfServiceUrl(swaggerConfigurationProperties.getTermsOfServiceUrl())
                .license(swaggerConfigurationProperties.getLicense())
                .licenseUrl(swaggerConfigurationProperties.getLicenseUrl())
                .contact(contact)
                .build();
    }

    private ApiKey getApiKey(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new ApiKey("JWT", swaggerConfigurationProperties.getAuthorizationHeader(), "header");
    }

    private SecurityContext getSecurityContext(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return SecurityContext.builder()
                .securityReferences(getDefaultAuth())
                .forPaths(PathSelectors.ant(swaggerConfigurationProperties.getPathSelectors()))
                .build();
    }

    private List<SecurityReference> getDefaultAuth() {

        AuthorizationScope authorizationScope
                = new AuthorizationScope("global", "accessEverything");

        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];

        authorizationScopes[0] = authorizationScope;

        return List.of(
                new SecurityReference("JWT", authorizationScopes));
    }
}
