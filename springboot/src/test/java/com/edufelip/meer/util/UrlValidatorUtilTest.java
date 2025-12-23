package com.edufelip.meer.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UrlValidatorUtilTest {

  @Test
  void acceptsHttpAndHttps() {
    assertDoesNotThrow(() -> UrlValidatorUtil.ensureHttpUrl("http://example.com", "site"));
    assertDoesNotThrow(() -> UrlValidatorUtil.ensureHttpUrl("https://example.com/path", "site"));
  }

  @Test
  void rejectsJavascriptScheme() {
    assertThrows(
        IllegalArgumentException.class,
        () -> UrlValidatorUtil.ensureHttpUrl("javascript:alert(1)", "site"));
  }

  @Test
  void rejectsRelativeUrl() {
    assertThrows(
        IllegalArgumentException.class, () -> UrlValidatorUtil.ensureHttpUrl("/foo", "site"));
  }
}
