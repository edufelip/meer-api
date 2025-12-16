package com.edufelip.meer.security;

import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SanitizingJacksonModuleConfig {

    private static final int MAX_INCOMING_STRING_LENGTH = 2048;

    @Bean
    public Module sanitizingStringModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new SanitizingStringDeserializer(MAX_INCOMING_STRING_LENGTH));
        return module;
    }
}
