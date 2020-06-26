package com.NowakArtur97.GlobalTerrorismAPI.configuration;

import com.NowakArtur97.GlobalTerrorismAPI.property.SwaggerConfigurationProperties;
import com.NowakArtur97.GlobalTerrorismAPI.tag.EventTag;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupEventsTag;
import com.NowakArtur97.GlobalTerrorismAPI.tag.GroupTag;
import com.NowakArtur97.GlobalTerrorismAPI.tag.TargetTag;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.RestController;
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
@Import({BeanValidatorPluginsConfiguration.class, BulkApiConfiguration.class})
public class SwaggerConfiguration {

    @Bean
    public Docket docket(SwaggerConfigurationProperties swaggerConfigurationProperties) {

        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.withClassAnnotation(RestController.class))
                .paths(PathSelectors.ant(swaggerConfigurationProperties.getPathSelectors()))
                .build()
                .apiInfo(apiDetails(swaggerConfigurationProperties))
                .tags(new Tag(TargetTag.RESOURCE, TargetTag.DESCRIPTION),
                        new Tag(EventTag.RESOURCE, EventTag.DESCRIPTION),
                        new Tag(GroupTag.RESOURCE, GroupTag.DESCRIPTION),
                        new Tag(GroupEventsTag.RESOURCE, GroupEventsTag.DESCRIPTION));
    }

    private ApiInfo apiDetails(SwaggerConfigurationProperties swaggerConfigurationProperties) {

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
}
