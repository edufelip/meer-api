package com.edufelip.meer.core.support;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "support_contact")
public class SupportContact {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(nullable = false)
  private String name;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String message;

  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public SupportContact() {}

  public SupportContact(String name, String email, String message) {
    this.name = name;
    this.email = email;
    this.message = message;
  }

  @PrePersist
  public void onCreate() {
    if (createdAt == null) {
      createdAt = Instant.now();
    }
  }

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String getMessage() {
    return message;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public void setName(String name) {
    this.name = name;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
