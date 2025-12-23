package com.edufelip.meer.dto;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;

@JsonTest
class StoreRatingDtoJsonTest {

  @Autowired private JacksonTester<StoreRatingDto> json;

  @Test
  void serializesToExpectedJson() throws Exception {
    UUID storeId = UUID.fromString("11111111-1111-1111-1111-111111111111");
    Instant createdAt = Instant.parse("2024-01-02T10:15:30Z");

    StoreRatingDto dto = new StoreRatingDto(7, storeId, 5, "Great", "Ana", null, createdAt);

    var content = json.write(dto);

    assertThat(content).extractingJsonPathNumberValue("$.id").isEqualTo(7);
    assertThat(content).extractingJsonPathStringValue("$.storeId").isEqualTo(storeId.toString());
    assertThat(content).extractingJsonPathNumberValue("$.score").isEqualTo(5);
    assertThat(content).extractingJsonPathStringValue("$.body").isEqualTo("Great");
    assertThat(content).extractingJsonPathStringValue("$.authorName").isEqualTo("Ana");
    assertThat(content)
        .extractingJsonPathStringValue("$.createdAt")
        .isEqualTo("2024-01-02T10:15:30Z");
    assertThat(content.getJson()).contains("\"authorAvatarUrl\":null");
  }
}
