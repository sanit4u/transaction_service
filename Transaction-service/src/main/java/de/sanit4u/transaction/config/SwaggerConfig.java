package de.sanit4u.transaction.config;

import java.sql.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Lists;

import de.sanit4u.transaction.util.Constants;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * 
 * swagger config
 */
@EnableSwagger2
@Configuration
public class SwaggerConfig {

	@Value("${application.base.package}")
	private String basePackage;

	@Bean
	public Docket productApi() {
		Docket docket = new Docket(DocumentationType.SWAGGER_2).apiInfo(metaInfo()).pathMapping("/").apiInfo(apiInfo())
				.forCodeGeneration(true).genericModelSubstitutes(ResponseEntity.class)
				.ignoredParameterTypes(java.sql.Date.class)
				.directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
				.directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
				.directModelSubstitute(java.time.LocalDateTime.class, Date.class)
				.securityContexts(Lists.newArrayList(securityContext())).securitySchemes(Lists.newArrayList(apiKey()))
				.useDefaultResponseMessages(false);

		return docket.select().apis(RequestHandlerSelectors.basePackage(basePackage)).paths(PathSelectors.any())
				.build();
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder().title("Transaction API").version("1.0.0").build();
	}

	private ApiKey apiKey() {
		return new ApiKey(Constants.CONST_SWAGGER_API_KEY_NAME, Constants.CONST_SWAGGER_AUTHORIZATION_HEADER,
				Constants.CONST_HEADER);
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder().securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(Constants.CONST_SWAGGER_DEFAULT_INCLUDE_PATTERN)).build();
	}

	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(new SecurityReference(Constants.CONST_SWAGGER_API_KEY_NAME, authorizationScopes));
	}

	private ApiInfo metaInfo() {
		ApiInfoBuilder apiInfnewoBuilder = new ApiInfoBuilder();

		ApiInfo apiInfo = apiInfnewoBuilder.title(Constants.CONST_SWAGGER_API_TITLE)
				.description(Constants.CONST_SWAGGER_API_DESC).license(Constants.CONST_SWAGGER_API_LICENSE)
				.licenseUrl(Constants.CONST_SWAGGER_API_LICENSE_URL)
				.contact(new Contact(Constants.CONST_SWAGGER_API_CONTACT_NAME, Constants.CONST_SWAGGER_API_CONTACT_URL,
						Constants.CONST_SWAGGER_API_CONTACT_EMAIL))
				.termsOfServiceUrl(Constants.CONST_SWAGGER_API_TOS_URL).build();

		return apiInfo;
	}
}