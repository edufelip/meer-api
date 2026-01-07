package com.edufelip.meer.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.edufelip.meer.core.push.PushEnvironment;
import com.edufelip.meer.domain.repo.PushTokenRepository;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class DeletePushTokenUseCaseTest {

  @Test
  void deletesByDeviceWhenEnvironmentMissing() {
    PushTokenRepository repo = Mockito.mock(PushTokenRepository.class);
    UUID userId = UUID.randomUUID();

    when(repo.deleteByUserAndDevice(userId, "device-1")).thenReturn(2);

    DeletePushTokenUseCase useCase = new DeletePushTokenUseCase(repo);
    int deleted = useCase.execute(userId, "device-1", null);

    assertThat(deleted).isEqualTo(2);
    verify(repo).deleteByUserAndDevice(userId, "device-1");
  }

  @Test
  void deletesByDeviceAndEnvironment() {
    PushTokenRepository repo = Mockito.mock(PushTokenRepository.class);
    UUID userId = UUID.randomUUID();

    when(repo.deleteByUserDeviceAndEnvironment(userId, "device-2", PushEnvironment.PROD))
        .thenReturn(1);

    DeletePushTokenUseCase useCase = new DeletePushTokenUseCase(repo);
    int deleted = useCase.execute(userId, "device-2", PushEnvironment.PROD);

    assertThat(deleted).isEqualTo(1);
    verify(repo).deleteByUserDeviceAndEnvironment(userId, "device-2", PushEnvironment.PROD);
  }
}
