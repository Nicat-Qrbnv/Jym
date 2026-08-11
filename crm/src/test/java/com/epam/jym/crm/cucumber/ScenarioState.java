package com.epam.jym.crm.cucumber;

import io.cucumber.spring.ScenarioScope;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Component
@ScenarioScope
public class ScenarioState {

  private MvcResult result;
  private String lastCreatedUsername;

  public MvcResult getResult() {
    return result;
  }

  public void setResult(MvcResult result) {
    this.result = result;
  }

  public String getLastCreatedUsername() {
    return lastCreatedUsername;
  }

  public void setLastCreatedUsername(String lastCreatedUsername) {
    this.lastCreatedUsername = lastCreatedUsername;
  }
}
