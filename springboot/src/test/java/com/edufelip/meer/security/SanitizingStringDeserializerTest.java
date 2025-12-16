package com.edufelip.meer.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SanitizingStringDeserializerTest {

    private ObjectMapper mapper(int maxLen) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(String.class, new SanitizingStringDeserializer(maxLen));
        return new ObjectMapper().registerModule(module);
    }

    record Payload(String value) {}

    @Test
    void stripsHtmlTagsAndScripts() throws Exception {
        ObjectMapper m = mapper(2048);
        String json = "{\"value\":\"<script>alert(1)</script> hello\"}";
        Payload p = m.readValue(json, Payload.class);
        assertThat(p.value()).isEqualTo("alert(1) hello");
    }

    @Test
    void enforcesMaxLength() throws Exception {
        ObjectMapper m = mapper(5);
        String json = "{\"value\":\"0123456789\"}";
        Payload p = m.readValue(json, Payload.class);
        assertThat(p.value()).isEqualTo("01234");
    }
}
