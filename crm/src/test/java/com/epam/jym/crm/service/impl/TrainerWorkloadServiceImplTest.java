package com.epam.jym.crm.service.impl;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.epam.jym.crm.dto.ActionType;
import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.crm.entity.Trainer;
import com.epam.jym.crm.entity.Training;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.exception.DownstreamServiceException;
import com.epam.jym.crm.messaging.TrainerWorkloadPublisher;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.MDC;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

  @Mock private TrainerWorkloadPublisher workloadPublisher;

  private TrainerWorkloadServiceImpl service;

  @BeforeEach
  void setUp() {
    service = new TrainerWorkloadServiceImpl(workloadPublisher);
    MDC.clear();
  }

  @Test
  void sendWorkloadUpdateShouldMapTrainingAndPublishSingleTraining() {
    Training training =
        training(LocalDate.of(2026, 7, 8));
    MDC.put(TRACE_ID_MDC_KEY, "trace-42");

    service.sendWorkloadUpdate(training, ActionType.ADD);

    ArgumentCaptor<TrainerWorkloadUpdateRequest> requestCaptor =
        ArgumentCaptor.forClass(TrainerWorkloadUpdateRequest.class);
    verify(workloadPublisher).publish(requestCaptor.capture(), eq("trace-42"));
    assertThat(requestCaptor.getValue())
        .isEqualTo(
            new TrainerWorkloadUpdateRequest(
                "jane.doe",
                "Jane",
                "Doe",
                true,
                LocalDate.of(2026, 7, 8),
                75,
                ActionType.ADD,
                11L));
  }

  @Test
  void sendWorkloadUpdateShouldThrowWhenTrainingPayloadIsIncomplete() {
    assertThatThrownBy(() -> service.sendWorkloadUpdate((Training) null, ActionType.ADD))
        .isInstanceOf(DownstreamServiceException.class)
        .hasMessage("Training payload is incomplete for workload update");

    verifyNoInteractions(workloadPublisher);
  }

  private static Training training(
      LocalDate scheduledDate) {
    User user = new User();
    user.setUsername("jane.doe");
    user.setFirstName("Jane");
    user.setLastName("Doe");
    user.setActive(true);

    Trainer trainer = new Trainer();
    trainer.setUser(user);

    Training training = new Training();
    training.setId(11L);
    training.setTrainer(trainer);
    training.setScheduledDate(scheduledDate);
    training.setDurationInMinutes(75);
    return training;
  }
}
