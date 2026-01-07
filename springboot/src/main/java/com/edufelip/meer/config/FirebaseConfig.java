package com.edufelip.meer.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "firebase", name = "enabled", havingValue = "true")
public class FirebaseConfig {

  @Bean
  public FirebaseApp firebaseApp(FirebaseProperties properties) throws IOException {
    FirebaseOptions.Builder builder = FirebaseOptions.builder();
    GoogleCredentials credentials = resolveCredentials(properties);
    builder.setCredentials(credentials);
    String projectId = properties.getProjectId();
    if (projectId != null && !projectId.isBlank()) {
      builder.setProjectId(projectId.trim());
    }

    if (FirebaseApp.getApps().isEmpty()) {
      return FirebaseApp.initializeApp(builder.build());
    }
    return FirebaseApp.getInstance();
  }

  @Bean
  public FirebaseMessaging firebaseMessaging(FirebaseApp app) {
    return FirebaseMessaging.getInstance(app);
  }

  private GoogleCredentials resolveCredentials(FirebaseProperties properties) throws IOException {
    String json = properties.getCredentialsJson();
    if (json != null && !json.isBlank()) {
      try (InputStream stream =
          new ByteArrayInputStream(json.trim().getBytes(StandardCharsets.UTF_8))) {
        return GoogleCredentials.fromStream(stream);
      }
    }
    String path = properties.getCredentialsPath();
    if (path != null && !path.isBlank()) {
      try (InputStream stream = new FileInputStream(path.trim())) {
        return GoogleCredentials.fromStream(stream);
      }
    }
    return GoogleCredentials.getApplicationDefault();
  }
}
