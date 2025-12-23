package com.edufelip.meer.web;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.edufelip.meer.config.TestClockConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(com.edufelip.meer.web.testutil.ErrorTestController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import({RestExceptionHandler.class, TestClockConfig.class})
class ErrorHandlingContractTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void illegalArgumentMapsToBadRequestWithMessage() throws Exception {
    mockMvc
        .perform(get("/test/error/illegal"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("bad input"));
  }

  @Test
  void responseStatusExceptionMapsToStatusAndMessage() throws Exception {
    mockMvc
        .perform(get("/test/error/status"))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Not found"));
  }

  @Test
  void genericExceptionMapsToInternalServerError() throws Exception {
    mockMvc
        .perform(get("/test/error/runtime"))
        .andExpect(status().isInternalServerError())
        .andExpect(jsonPath("$.message").value("Internal server error"));
  }

  @Test
  void missingAuthorizationHeaderReturnsUnauthorized() throws Exception {
    mockMvc
        .perform(get("/test/error/auth"))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.message").value("Missing Authorization header"));
  }

  @Test
  void invalidPathVariableReturnsBadRequest() throws Exception {
    mockMvc
        .perform(get("/test/error/number/not-a-number"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Invalid request parameter"));
  }

  @Test
  void malformedJsonReturnsBadRequest() throws Exception {
    mockMvc
        .perform(
            post("/test/error/json").contentType(MediaType.APPLICATION_JSON).content("{\"value\":"))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.message").value("Malformed JSON request body"));
  }
}
