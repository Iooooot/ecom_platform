package com.db.ecom_platform.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Swagger配置类
 */
@Configuration
public class SwaggerConfig {
    
    @Value("${swagger.enable:true}")
    private boolean enableSwagger;
    
    @Bean
    public Docket userApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("用户API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.db.ecom_platform.controller"))
                .paths(PathSelectors.ant("/api/user/**").or(PathSelectors.ant("/api/addresses/**")))
                .build()
                .enable(enableSwagger)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }
    
    @Bean
    public Docket consumptionApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("消费统计API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.db.ecom_platform.controller"))
                .paths(PathSelectors.ant("/api/consumption/**"))
                .build()
                .enable(enableSwagger)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean
    public Docket alipayApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("支付宝API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.db.ecom_platform.controller"))
                .paths(PathSelectors.ant("/api/alipay/**"))
                .build()
                .enable(enableSwagger)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean
    public Docket adminApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("管理员API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.db.ecom_platform.controller"))
                .paths(PathSelectors.ant("/api/admin/**"))
                .build()
                .enable(enableSwagger)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }

    @Bean
    public Docket productApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("商品API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.db.ecom_platform.controller"))
                .paths(PathSelectors.ant("/api/products/**"))
                .build()
                .enable(enableSwagger)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }
    
    @Bean
    public Docket reviewApi() {
        return new Docket(DocumentationType.OAS_30)
                .apiInfo(apiInfo())
                .groupName("评价API")
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.db.ecom_platform.controller"))
                .paths(PathSelectors.ant("/api/reviews/**"))
                .build()
                .enable(enableSwagger)
                .securitySchemes(securitySchemes())
                .securityContexts(securityContexts());
    }
    
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("电商平台API文档")
                .description("电商平台接口文档，包含用户管理、地址管理、消费统计等功能")
                .version("1.0.0")
                .contact(new Contact("开发团队", "http://www.example.com", "dev@example.com"))
                .license("Apache 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0.html")
                .build();
    }
    
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> schemes = new ArrayList<>();
        // Bearer 认证
        schemes.add(new ApiKey("Authorization", "Authorization", "header"));
        return schemes;
    }
    
    private List<SecurityContext> securityContexts() {
        return Collections.singletonList(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .forPaths(PathSelectors.regex("^(?!auth).*$"))
                        .build()
        );
    }
    
    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        return Collections.singletonList(
                new SecurityReference("Authorization", authorizationScopes)
        );
    }
} 