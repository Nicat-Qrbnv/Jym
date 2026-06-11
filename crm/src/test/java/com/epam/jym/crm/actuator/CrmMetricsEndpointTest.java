package com.epam.jym.crm.actuator;

import static org.mockito.Mockito.when;

import com.epam.jym.crm.repository.SpecializationTrainerCount;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.repository.UserRepository;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CrmMetricsEndpointTest {

  @Mock private TrainerRepository trainerRepository;

  @Mock private UserRepository userRepository;

  @InjectMocks private CrmMetricsEndpoint crmMetricsEndpoint;

  @Test
  void metricsShouldReturnActiveUsersAndTrainersBySpecialization() {
    when(userRepository.countByIsActiveTrue()).thenReturn(13L);
    when(trainerRepository.countActiveTrainersBySpecialization())
        .thenReturn(
            List.of(
                specializationCount("Non-technical"),
                specializationCount("Technical")));

    CrmMetricsEndpoint.CrmMetrics metrics = crmMetricsEndpoint.metrics();

    Assertions.assertThat(metrics.activeUsers()).isEqualTo(13L);
    Assertions.assertThat(metrics.trainersBySpecialization())
        .containsEntry("Non-technical", 2L)
        .containsEntry("Technical", 2L);
  }

  private static SpecializationTrainerCount specializationCount(
      String specialization) {
    return new SpecializationTrainerCount() {
      @Override
      public String getSpecializationType() {
        return specialization;
      }

      @Override
      public long getTrainerCount() {
        return 2L;
      }
    };
  }
}
