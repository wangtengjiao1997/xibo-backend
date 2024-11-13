package com.npc.old_school.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI oldSchoolOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("老年大学管理系统 API")
                        .description("老年大学管理系统后端服务接口文档")
                        .version("v0.0.3")
                        .contact(new Contact()
                                .name("NPC")
                                .email("support@example.com")));
    }
} 