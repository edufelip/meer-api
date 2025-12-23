package com.edufelip.meer.mapper;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.domain.auth.AuthenticatedUser;
import com.edufelip.meer.dto.AuthDtos;

public class AuthMappers {
  public static AuthDtos.UserDto toDto(AuthenticatedUser user) {
    var role =
        user.getRole() != null
            ? user.getRole().name()
            : com.edufelip.meer.core.auth.Role.USER.name();
    return new AuthDtos.UserDto(
        String.valueOf(user.getId()), user.getName(), user.getEmail(), role);
  }

  public static AuthDtos.UserDto toDto(AuthUser user) {
    var role =
        user.getRole() != null
            ? user.getRole().name()
            : com.edufelip.meer.core.auth.Role.USER.name();
    return new AuthDtos.UserDto(
        String.valueOf(user.getId()), user.getDisplayName(), user.getEmail(), role);
  }
}
