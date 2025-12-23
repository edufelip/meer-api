package com.edufelip.meer.core.category;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "category")
public class Category {
  @Id
  @Column(length = 64, nullable = false, updatable = false)
  private String id;

  @Column(name = "name_string_id", nullable = false, unique = true)
  private String nameStringId;

  @Column(name = "image_res_id", nullable = false)
  private String imageResId;

  @CreationTimestamp
  @Column(name = "created_at", nullable = false, updatable = false)
  private Instant createdAt;

  public Category() {}

  public Category(String id, String nameStringId, String imageResId) {
    this.id = id;
    this.nameStringId = nameStringId;
    this.imageResId = imageResId;
  }

  public String getId() {
    return id;
  }

  public String getNameStringId() {
    return nameStringId;
  }

  public String getImageResId() {
    return imageResId;
  }

  public Instant getCreatedAt() {
    return createdAt;
  }

  public void setId(String id) {
    this.id = id;
  }

  public void setNameStringId(String nameStringId) {
    this.nameStringId = nameStringId;
  }

  public void setImageResId(String imageResId) {
    this.imageResId = imageResId;
  }

  public void setCreatedAt(Instant createdAt) {
    this.createdAt = createdAt;
  }
}
