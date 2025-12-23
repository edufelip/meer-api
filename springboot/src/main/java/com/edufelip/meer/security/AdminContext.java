package com.edufelip.meer.security;

import com.edufelip.meer.core.auth.AuthUser;
import java.util.Optional;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

/** Utility to access the admin user placed on the request by DashboardAdminGuardFilter. */
public final class AdminContext {
  private static final String KEY = "adminUser";

  private AdminContext() {}

  public static Optional<AuthUser> currentAdmin() {
    try {
      var attrs = RequestContextHolder.currentRequestAttributes();
      Object cached = attrs.getAttribute(KEY, RequestAttributes.SCOPE_REQUEST);
      if (cached instanceof AuthUser user) return Optional.of(user);
    } catch (IllegalStateException ignored) {
    }
    return Optional.empty();
  }
}
