package com.NowakArtur97.GlobalTerrorismAPI.configuration;

import java.util.Collections;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	@Bean
	public Docket docket() {

		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.NowakArtur97.GlobalTerrorismAPI.controller"))
				.paths(PathSelectors.ant("/api/**")).build().apiInfo(apiDetails());
	}

	private ApiInfo apiDetails() {

		Contact contact = new Contact("Artur Nowak", "https://github.com/NowakArtur97", "");

		return new ApiInfo("Global Terrorism API", 
				"REST API providing information on terrorist attacks", 
				"1.0",
				"Free to use", 
				contact, 
				"MIT", 
				"https://github.com/NowakArtur97/GlobalTerrorismAPI/blob/master/LICENSE",
				Collections.emptyList());
	}
}
