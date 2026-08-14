package com.epam.jym.crm.cucumber;

import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import io.cucumber.spring.ScenarioScope;
import java.util.List;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MvcResult;

@Setter
@Getter
@Component
@ScenarioScope
public class ScenarioState {

  private MvcResult result;
  private String lastCreatedUsername;
  private TrainerWorkloadUpdateRequest lastWorkloadMessage;
  private List<TrainerWorkloadUpdateRequest> lastWorkloadMessages;

}
