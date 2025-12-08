package com.edufelip.meer.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilderCustomizer;

@Configuration
public class JacksonSanitizerConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer sanitizeStrings() {
        return builder -> builder.deserializerByType(String.class, new SanitizingStringDeserializer());
    }
}
