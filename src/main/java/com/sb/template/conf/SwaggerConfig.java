package com.sb.template.conf;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfig {

	private static final String API_NAME = "Templete-Springboot API";
    private static final String API_VERSION = "0.0.1";
    private static final String API_DESCRIPTION = "Templete-Springboot API Doc";

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.sb.template.controller"))
                .paths(PathSelectors.ant("/reply/**"))
                .build();
    }

    public ApiInfo apiInfo() {

    	return new ApiInfoBuilder()
    			.title(API_NAME)
    			.version(API_VERSION)
    			.description(API_DESCRIPTION)
    			.contact(new Contact("Koiking", "https://koiking.tistory.com/", "test-email@gmail.com"))
    			.build();
    }
}
