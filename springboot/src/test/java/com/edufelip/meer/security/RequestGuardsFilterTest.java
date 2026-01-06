package com.edufelip.meer.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.auth.Role;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.security.guards.AppHeaderGuard;
import com.edufelip.meer.security.guards.FirebaseAuthGuard;
import com.edufelip.meer.security.token.TokenPayload;
import com.edufelip.meer.security.token.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestGuardsFilterTest {

  @Test
  void publicPathsBypassAuthButRequireAppHeader() throws ServletException, IOException {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(false);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest loginRequest = new MockHttpServletRequest("POST", "/auth/login");
    loginRequest.setServletPath("/auth/login");
    MockHttpServletResponse loginResponse = new MockHttpServletResponse();
    filter.doFilter(loginRequest, loginResponse, chain);
    assertThat(chainCalled.get()).isTrue();

    chainCalled.set(false);
    MockHttpServletRequest contentsRequest = new MockHttpServletRequest("GET", "/contents");
    contentsRequest.setServletPath("/contents");
    MockHttpServletResponse contentsResponse = new MockHttpServletResponse();
    filter.doFilter(contentsRequest, contentsResponse, chain);
    assertThat(chainCalled.get()).isFalse();
    assertThat(contentsResponse.getStatus()).isEqualTo(401);

    chainCalled.set(false);
    contentsRequest = new MockHttpServletRequest("GET", "/contents");
    contentsRequest.setServletPath("/contents");
    contentsRequest.addHeader(AppHeaderGuard.APP_HEADER, "com.edufelip.meer");
    contentsResponse = new MockHttpServletResponse();
    filter.doFilter(contentsRequest, contentsResponse, chain);
    assertThat(chainCalled.get()).isTrue();
  }

  @Test
  void publicPathWithInvalidTokenIsRejected() throws Exception {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(false);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/contents");
    request.setServletPath("/contents");
    request.addHeader(AppHeaderGuard.APP_HEADER, "com.edufelip.meer");
    request.addHeader(FirebaseAuthGuard.AUTH_HEADER, "Bearer bad");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  void nonPublicPathsRequireHeaders() throws ServletException, IOException {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(false);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stores");
    request.setServletPath("/stores");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  void validHeadersAllowNonPublicRequests() throws ServletException, IOException {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(false);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stores");
    request.setServletPath("/stores");
    request.addHeader(AppHeaderGuard.APP_HEADER, "com.edufelip.meer");
    request.addHeader(FirebaseAuthGuard.AUTH_HEADER, "Bearer token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void missingAppHeaderIsRejected() throws ServletException, IOException {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(false);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stores");
    request.setServletPath("/stores");
    request.addHeader(FirebaseAuthGuard.AUTH_HEADER, "Bearer token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  void wrongAppHeaderIsRejected() throws ServletException, IOException {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(false);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stores");
    request.setServletPath("/stores");
    request.addHeader(AppHeaderGuard.APP_HEADER, "com.other.app");
    request.addHeader(FirebaseAuthGuard.AUTH_HEADER, "Bearer token");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  void malformedBearerIsRejected() throws ServletException, IOException {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(false);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stores");
    request.setServletPath("/stores");
    request.addHeader(AppHeaderGuard.APP_HEADER, "com.edufelip.meer");
    request.addHeader(FirebaseAuthGuard.AUTH_HEADER, "Bearer");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isFalse();
    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  void disableAuthSkipsBearerValidationButStillRequiresAppHeader() throws Exception {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(true);
    props.setDisableAuth(true);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stores");
    request.setServletPath("/stores");
    request.addHeader(AppHeaderGuard.APP_HEADER, "com.edufelip.meer");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  @Test
  void disableAuthAndNoAppHeaderAllowsRequestWhenHeaderNotRequired() throws Exception {
    SecurityProperties props = new SecurityProperties();
    props.setRequireAppHeader(false);
    props.setDisableAuth(true);
    props.setAppPackage("com.edufelip.meer");

    RequestGuardsFilter filter = buildFilter(props);
    AtomicBoolean chainCalled = new AtomicBoolean(false);
    FilterChain chain =
        (request, response) -> {
          chainCalled.set(true);
        };

    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/stores");
    request.setServletPath("/stores");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, chain);

    assertThat(chainCalled.get()).isTrue();
    assertThat(response.getStatus()).isEqualTo(200);
  }

  private RequestGuardsFilter buildFilter(SecurityProperties props) {
    TokenProvider tokenProvider = mock(TokenProvider.class);
    AuthUserRepository authUserRepository = mock(AuthUserRepository.class);
    UUID userId = UUID.randomUUID();
    when(tokenProvider.parseAccessToken("token"))
        .thenReturn(new TokenPayload(userId, "user@example.com", "User", Role.USER));
    when(tokenProvider.parseAccessToken("bad")).thenThrow(new RuntimeException("bad"));
    AuthUser user = new AuthUser();
    user.setId(userId);
    user.setRole(Role.USER);
    when(authUserRepository.findById(userId)).thenReturn(Optional.of(user));
    return new RequestGuardsFilter(props, tokenProvider, authUserRepository);
  }
}
