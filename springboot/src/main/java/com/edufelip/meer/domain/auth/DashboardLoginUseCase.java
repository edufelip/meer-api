package com.edufelip.meer.domain.auth;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.auth.Role;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.security.token.TokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;

public class DashboardLoginUseCase {
  private final AuthUserRepository authUserRepository;
  private final PasswordEncoder passwordEncoder;
  private final TokenProvider tokenProvider;

  public DashboardLoginUseCase(
      AuthUserRepository authUserRepository,
      PasswordEncoder passwordEncoder,
      TokenProvider tokenProvider) {
    this.authUserRepository = authUserRepository;
    this.passwordEncoder = passwordEncoder;
    this.tokenProvider = tokenProvider;
  }

  public AuthResult execute(String email, String password) {
    AuthUser user = authUserRepository.findByEmail(email);
    if (user == null) throw new InvalidCredentialsException();
    if (user.getRole() == null || user.getRole() != Role.ADMIN) throw new NonAdminUserException();
    if (!passwordEncoder.matches(password, user.getPasswordHash()))
      throw new InvalidCredentialsException();

    String access = tokenProvider.generateAccessToken(user);
    String refresh = tokenProvider.generateRefreshToken(user);

    AuthenticatedUser authUser =
        new AuthenticatedUser(user.getId(), user.getDisplayName(), user.getEmail(), user.getRole());
    return new AuthResult(access, refresh, authUser);
  }
}
