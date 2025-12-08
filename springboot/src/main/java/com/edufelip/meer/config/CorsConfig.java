package com.edufelip.meer.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    private final List<String> allowedOrigins;
    private final boolean allowCredentials;

    public CorsConfig(@Value("${meer.cors.allowed-origins:}") String originsProp) {
        if (StringUtils.hasText(originsProp)) {
            this.allowedOrigins = Arrays.stream(originsProp.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .toList();
            this.allowCredentials = true; // only set when explicit origins are provided
        } else {
            this.allowedOrigins = List.of("*");
            this.allowCredentials = false;
        }
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(allowedOrigins.toArray(String[]::new))
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("Authorization", "Content-Type", "Accept", "X-Requested-With", "X-App-Package")
                .allowCredentials(allowCredentials)
                .maxAge(3600);
    }
}
