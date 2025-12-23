package com.edufelip.meer.domain.auth;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.dto.UpdateProfileRequest;
import com.edufelip.meer.security.token.InvalidTokenException;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import org.springframework.util.StringUtils;

public class UpdateProfileUseCase {
  private final TokenProvider tokenProvider;
  private final AuthUserRepository authUserRepository;

  public UpdateProfileUseCase(TokenProvider tokenProvider, AuthUserRepository authUserRepository) {
    this.tokenProvider = tokenProvider;
    this.authUserRepository = authUserRepository;
  }

  public AuthUser execute(String accessToken, UpdateProfileRequest request) {
    TokenPayload payload;
    try {
      payload = tokenProvider.parseAccessToken(accessToken);
    } catch (RuntimeException ex) {
      throw new InvalidTokenException();
    }

    AuthUser user =
        authUserRepository.findById(payload.getUserId()).orElseThrow(InvalidTokenException::new);

    if (StringUtils.hasText(request.name())) user.setDisplayName(request.name());
    if (request.avatarUrl() != null) user.setPhotoUrl(request.avatarUrl());
    if (request.bio() != null) user.setBio(request.bio());
    if (request.notifyNewStores() != null) user.setNotifyNewStores(request.notifyNewStores());
    if (request.notifyPromos() != null) user.setNotifyPromos(request.notifyPromos());

    return authUserRepository.save(user);
  }
}
