package com.edufelip.meer.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class SanitizingHttpServletRequestWrapper extends HttpServletRequestWrapper {

  public SanitizingHttpServletRequestWrapper(HttpServletRequest request) {
    super(request);
  }

  @Override
  public String getParameter(String name) {
    return Sanitizer.sanitize(super.getParameter(name));
  }

  @Override
  public String[] getParameterValues(String name) {
    String[] values = super.getParameterValues(name);
    if (values == null) return null;
    String[] sanitized = new String[values.length];
    for (int i = 0; i < values.length; i++) {
      sanitized[i] = Sanitizer.sanitize(values[i]);
    }
    return sanitized;
  }

  @Override
  public String getHeader(String name) {
    return Sanitizer.sanitize(super.getHeader(name));
  }
}
