package com.ironhack.bank_system.configurations.swagger;

import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;
import springfox.documentation.service.Contact;

import java.util.ArrayList;
import java.util.Arrays;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

    private static final String PATH = "/";
    private static final String COMPANY = "CRYPTO INTERNATIONAL BANK";
    private static final String WEBSITE = "http://www.cib.com/";
    private static final String EMAIL = "alianyceo@cib.com";
    private static final String API = "core-api";
    private static final String VERSION = "1.0";
    private static final String TITLE = "Core Server Api Rest-V1";
    private static final String DESCRIPTION = "Es un banco español de crytomonedas con sede en Madrid. Es una de las mayores e importantes entidades financieras del mundo";

    private static final Contact DEFAULT_CONTACT = new Contact(COMPANY, WEBSITE, EMAIL);
    private static final ApiInfo DEFAULT_API_INFO = new ApiInfoBuilder()
            .title(TITLE)
            .description(DESCRIPTION)
            .version(VERSION)
            .termsOfServiceUrl("urn:tos")
            .contact(DEFAULT_CONTACT)
            .license("Apache 2.0")
            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0")
            .extensions(new ArrayList<>())
            .build();

    /**
     * News api docket.
     *
     * @return the docket
     */
    @Bean
    public Docket IdentityServerApi() {
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName(API + ": " + VERSION)
                .pathMapping(PATH)
                .select()
                .apis(RequestHandlerSelectors.any())
                .paths(PathSelectors.any())
//                .paths(PathSelectors.ant(PATH + "**"))
                .build()
                .protocols(
                        Sets.newHashSet(
                                Arrays.asList(
                                        "http",
                                        "https"
                                )
                        )
                )
//                .securitySchemes(Lists.newArrayList(apiKey()))
//                .securityContexts(Lists.newArrayList(securityContext()))
                .apiInfo(DEFAULT_API_INFO);
    }
}
