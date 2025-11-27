package com.edufelip.meer.config;

import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile({"local-db", "local", "default"})
public class OpenApiConfig {

    @Bean
    public GroupedOpenApi apiDocs() {
        return GroupedOpenApi.builder()
                .group("meer")
                .pathsToMatch("/**")
                .build();
    }
}
