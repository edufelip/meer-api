package com.edufelip.meer.security;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SanitizingJacksonModuleConfig {

    @Bean
    public Module sanitizingStringModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new SanitizingStringDeserializer());
        return module;
    }
}
