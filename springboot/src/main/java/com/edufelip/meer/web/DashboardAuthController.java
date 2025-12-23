package com.edufelip.meer.web;

import com.edufelip.meer.domain.auth.DashboardLoginUseCase;
import com.edufelip.meer.domain.auth.InvalidCredentialsException;
import com.edufelip.meer.domain.auth.NonAdminUserException;
import com.edufelip.meer.dto.AuthDtos;
import com.edufelip.meer.mapper.AuthMappers;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard")
public class DashboardAuthController {

  private final DashboardLoginUseCase dashboardLoginUseCase;

  public DashboardAuthController(DashboardLoginUseCase dashboardLoginUseCase) {
    this.dashboardLoginUseCase = dashboardLoginUseCase;
  }

  @PostMapping("/login")
  public ResponseEntity<AuthDtos.AuthResponse> dashboardLogin(
      @RequestBody AuthDtos.LoginRequest body) {
    var result = dashboardLoginUseCase.execute(body.email(), body.password());
    return ResponseEntity.ok(
        new AuthDtos.AuthResponse(
            result.getToken(), result.getRefreshToken(), AuthMappers.toDto(result.getUser())));
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  public ResponseEntity<Map<String, String>> handleInvalidCredentials(
      InvalidCredentialsException ex) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("message", ex.getMessage()));
  }

  @ExceptionHandler(NonAdminUserException.class)
  public ResponseEntity<Map<String, String>> handleNonAdmin(NonAdminUserException ex) {
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("message", ex.getMessage()));
  }
}
