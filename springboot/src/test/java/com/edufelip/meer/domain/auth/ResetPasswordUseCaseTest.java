package com.edufelip.meer.domain.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.edufelip.meer.config.TestClockConfig;
import com.edufelip.meer.core.auth.AuthUser;
import com.edufelip.meer.core.auth.PasswordResetToken;
import com.edufelip.meer.domain.repo.AuthUserRepository;
import com.edufelip.meer.domain.repo.PasswordResetTokenRepository;
import com.edufelip.meer.support.TestFixtures;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Import(TestClockConfig.class)
@Transactional
class ResetPasswordUseCaseTest {

  @Autowired private AuthUserRepository authUserRepository;
  @Autowired private PasswordResetTokenRepository passwordResetTokenRepository;
  @Autowired private EntityManager entityManager;
  @Autowired private ResetPasswordUseCase useCase;
  @Autowired private PasswordEncoder passwordEncoder;

  @Test
  void resetsPasswordAndMarksTokenUsed() {
    AuthUser user = TestFixtures.user("jane@example.com", "Jane");
    user.setPasswordHash(passwordEncoder.encode("OldPass1!"));
    authUserRepository.save(user);

    UUID tokenId = UUID.randomUUID();
    Instant expiresAt = TestClockConfig.FIXED_INSTANT.plusSeconds(1800);
    passwordResetTokenRepository.save(new PasswordResetToken(tokenId, user, expiresAt));

    useCase.execute(tokenId.toString(), "NewPass1!");

    entityManager.flush();
    entityManager.clear();

    AuthUser updatedUser = authUserRepository.findById(user.getId()).orElseThrow();
    assertThat(passwordEncoder.matches("NewPass1!", updatedUser.getPasswordHash())).isTrue();

    PasswordResetToken updatedToken = passwordResetTokenRepository.findById(tokenId).orElseThrow();
    assertThat(updatedToken.getUsedAt()).isEqualTo(TestClockConfig.FIXED_INSTANT);
  }

  @Test
  void rejectsExpiredToken() {
    AuthUser user = TestFixtures.user("jane@example.com", "Jane");
    authUserRepository.save(user);

    UUID tokenId = UUID.randomUUID();
    Instant expiresAt = TestClockConfig.FIXED_INSTANT.minusSeconds(60);
    passwordResetTokenRepository.save(new PasswordResetToken(tokenId, user, expiresAt));

    assertThatThrownBy(() -> useCase.execute(tokenId.toString(), "NewPass1!"))
        .isInstanceOf(ResetPasswordException.class)
        .hasMessage("Reset token has expired.");

    PasswordResetToken storedToken = passwordResetTokenRepository.findById(tokenId).orElseThrow();
    assertThat(storedToken.getUsedAt()).isNull();
  }

  @Test
  void rejectsInvalidPassword() {
    AuthUser user = TestFixtures.user("jane@example.com", "Jane");
    authUserRepository.save(user);

    UUID tokenId = UUID.randomUUID();
    Instant expiresAt = TestClockConfig.FIXED_INSTANT.plusSeconds(1800);
    passwordResetTokenRepository.save(new PasswordResetToken(tokenId, user, expiresAt));

    assertThatThrownBy(() -> useCase.execute(tokenId.toString(), "weak"))
        .isInstanceOf(ResetPasswordException.class)
        .hasMessage(
            "Password must be at least 6 characters and include an uppercase letter, a number, and a special character.");

    PasswordResetToken storedToken = passwordResetTokenRepository.findById(tokenId).orElseThrow();
    assertThat(storedToken.getUsedAt()).isNull();
  }

  @Test
  void rejectsUsedToken() {
    AuthUser user = TestFixtures.user("jane@example.com", "Jane");
    authUserRepository.save(user);

    UUID tokenId = UUID.randomUUID();
    Instant expiresAt = TestClockConfig.FIXED_INSTANT.plusSeconds(1800);
    passwordResetTokenRepository.save(new PasswordResetToken(tokenId, user, expiresAt));

    useCase.execute(tokenId.toString(), "NewPass1!");

    assertThatThrownBy(() -> useCase.execute(tokenId.toString(), "Other1!"))
        .isInstanceOf(ResetPasswordException.class)
        .hasMessage("Reset token has already been used.");
  }
}
