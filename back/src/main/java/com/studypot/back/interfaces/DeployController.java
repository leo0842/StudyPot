package com.studypot.back.interfaces;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class DeployController {

  private final Environment env;

  @GetMapping("/health")
  public String checkHealth() {

    return env.getProperty("deploy.name");
  }
}
