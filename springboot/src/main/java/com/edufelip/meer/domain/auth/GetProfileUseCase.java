package com.edufelip.meer.domain.auth;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.security.token.InvalidTokenException;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;

public class GetProfileUseCase {
  private final TokenProvider tokenProvider;
  private final AuthUserRepository authUserRepository;

  public GetProfileUseCase(TokenProvider tokenProvider, AuthUserRepository authUserRepository) {
    this.tokenProvider = tokenProvider;
    this.authUserRepository = authUserRepository;
  }

  public AuthUser execute(String accessToken) {
    TokenPayload payload;
    try {
      payload = tokenProvider.parseAccessToken(accessToken);
    } catch (RuntimeException ex) {
      throw new InvalidTokenException();
    }
    return authUserRepository.findById(payload.getUserId()).orElseThrow(InvalidTokenException::new);
  }
}
