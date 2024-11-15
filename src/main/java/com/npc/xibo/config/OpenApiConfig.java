package com.npc.xibo.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
    @Bean
    public OpenAPI xiboOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("喜播 API")
                        .description("喜播")
                        .version("v0.0.3")
                        .contact(new Contact()
                                .name("NPC")
                                .email("support@example.com")));
    }
}