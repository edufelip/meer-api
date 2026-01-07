package com.edufelip.meer.core.push;

import com.edufelip.meer.util.Uuid7;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Entity
@Table(
    name = "push_token",
    uniqueConstraints =
        @UniqueConstraint(columnNames = {"auth_user_id", "device_id", "environment"}))
public class PushToken {
  @Id
  @Column(columnDefinition = "uuid")
  private UUID id;

  @Column(name = "auth_user_id", columnDefinition = "uuid", nullable = false)
  private UUID userId;

  @Column(name = "device_id", nullable = false, length = 255)
  private String deviceId;

  @Column(name = "fcm_token", nullable = false, length = 4096)
  private String fcmToken;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private PushPlatform platform;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 16)
  private PushEnvironment environment;

  @Column(name = "app_version", length = 64)
  private String appVersion;

  @Column(name = "last_seen_at", nullable = false)
  private Instant lastSeenAt;

  @Column(name = "last_token_refresh_at")
  private Instant lastTokenRefreshAt;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Instant createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Instant updatedAt;

  public PushToken() {}

  public UUID getId() {
    return id;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getDeviceId() {
    return deviceId;
  }

  public String getFcmToken() {
    return fcmToken;
  }

  public PushPlatform getPlatform() {
    return platform;
  }

  public PushEnvironment getEnvironment() {
    return environment;
  }

  public String getAppVersion() {
    return appVersion;
  }

  public Instant getLastSeenAt() {
    return lastSeenAt;
  }

  public Instant getLastTokenRefreshAt() {
    return lastTokenRefreshAt;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public Instant getUpdatedAt() {
    return updatedAt;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  public void setDeviceId(String deviceId) {
    this.deviceId = deviceId;
  }

  public void setFcmToken(String fcmToken) {
    this.fcmToken = fcmToken;
  }

  public void setPlatform(PushPlatform platform) {
    this.platform = platform;
  }

  public void setEnvironment(PushEnvironment environment) {
    this.environment = environment;
  }

  public void setAppVersion(String appVersion) {
    this.appVersion = appVersion;
  }

  public void setLastSeenAt(Instant lastSeenAt) {
    this.lastSeenAt = lastSeenAt;
  }

  public void setLastTokenRefreshAt(Instant lastTokenRefreshAt) {
    this.lastTokenRefreshAt = lastTokenRefreshAt;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }

  public void setUpdatedAt(Instant updatedAt) {
    this.updatedAt = updatedAt;
  }

  @PrePersist
  public void ensureId() {
    if (this.id == null) {
      this.id = Uuid7.next();
    }
  }
}
