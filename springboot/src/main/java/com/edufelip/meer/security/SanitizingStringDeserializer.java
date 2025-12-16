package com.edufelip.meer.security;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;

import java.io.IOException;

/**
 * Global string sanitizer used on request bodies to strip HTML/JS payloads,
 * trim whitespace, and enforce a maximum length to mitigate XSS/DoS vectors.
 */
public class SanitizingStringDeserializer extends StdScalarDeserializer<String> {

    private final int maxLength;

    public SanitizingStringDeserializer(int maxLength) {
        super(String.class);
        this.maxLength = maxLength;
    }

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = p.getValueAsString();
        if (value == null) return null;

        String cleaned = Jsoup.clean(value, Safelist.none()).trim();
        if (cleaned.length() > maxLength) {
            cleaned = cleaned.substring(0, maxLength);
        }
        return cleaned;
    }
}
