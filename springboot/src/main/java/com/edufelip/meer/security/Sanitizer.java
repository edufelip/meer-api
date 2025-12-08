package com.edufelip.meer.security;

import java.text.Normalizer;

public final class Sanitizer {

    private Sanitizer() {}

    /**
     * Light-weight input sanitizer to strip control chars and basic script tags.
     * Does not HTML-escape (so stored text stays readable) but removes common XSS vectors.
     */
    public static String sanitize(String input) {
        if (input == null) return null;

        String value = Normalizer.normalize(input, Normalizer.Form.NFKC).trim();

        // Remove non-printable control chars except CR/LF/TAB
        value = value.replaceAll("[\\p{Cntrl}&&[^\\r\\n\\t]]", "");

        // Strip <script> and </script> tags case-insensitively
        value = value.replaceAll("(?i)<\\s*script[^>]*>", "");
        value = value.replaceAll("(?i)</\\s*script\\s*>", "");

        return value;
    }
}
