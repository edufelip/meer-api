package com.edufelip.meer.util;

import java.net.URI;

/** Minimal URL validator to guard against javascript: or data: URLs and require http/https. */
public final class UrlValidatorUtil {
  private UrlValidatorUtil() {}

  public static void ensureHttpUrl(String url, String fieldName) {
    if (url == null || url.isBlank()) return;
    String trimmed = url.trim();
    try {
      URI uri = URI.create(trimmed);
      String scheme = uri.getScheme();
      if (scheme == null
          || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
        throw new IllegalArgumentException(fieldName + " must be an http(s) URL");
      }
      if (!uri.isAbsolute()) {
        throw new IllegalArgumentException(fieldName + " must be an absolute URL");
      }
    } catch (IllegalArgumentException ex) {
      throw new IllegalArgumentException(fieldName + " must be an http(s) URL");
    }
  }
}
