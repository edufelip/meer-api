package com.edufelip.meer.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "firebase")
public class FirebaseProperties {
  private boolean enabled = false;
  private String projectId = "";
  private String credentialsPath = "";
  private String credentialsJson = "";

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public String getProjectId() {
    return projectId;
  }

  public void setProjectId(String projectId) {
    this.projectId = projectId;
  }

  public String getCredentialsPath() {
    return credentialsPath;
  }

  public void setCredentialsPath(String credentialsPath) {
    this.credentialsPath = credentialsPath;
  }

  public String getCredentialsJson() {
    return credentialsJson;
  }

  public void setCredentialsJson(String credentialsJson) {
    this.credentialsJson = credentialsJson;
  }
}
