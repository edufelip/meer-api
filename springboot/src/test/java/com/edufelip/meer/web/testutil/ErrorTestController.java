package com.edufelip.meer.web.testutil;

import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/test/error")
public class ErrorTestController {

  @GetMapping("/illegal")
  public Map<String, String> illegal() {
    throw new IllegalArgumentException("bad input");
  }

  @GetMapping("/status")
  public Map<String, String> status() {
    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Not found");
  }

  @GetMapping("/runtime")
  public Map<String, String> runtime() {
    throw new RuntimeException("boom");
  }

  @GetMapping("/auth")
  public Map<String, String> auth(@RequestHeader("Authorization") String authHeader) {
    return Map.of("ok", authHeader);
  }

  @GetMapping("/number/{id}")
  public Map<String, Integer> number(@PathVariable int id) {
    return Map.of("id", id);
  }

  @PostMapping("/json")
  public Map<String, String> json(@RequestBody Map<String, String> body) {
    return body;
  }
}
