package com.epam.jym.trainerworkload.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.epam.jym.trainerworkload.domain.MonthlyWorkloadAggregate;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import com.epam.jym.trainerworkload.dto.ActionType;
import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.exception.BusinessRuleViolationException;
import com.epam.jym.trainerworkload.repository.TrainerWorkloadAggregateRepository;
import com.epam.jym.trainerworkload.repository.TrainingWorkloadIndexRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class TrainerWorkloadServiceImplTest {

  @Mock private TrainerWorkloadAggregateRepository aggregateRepository;
  @Mock private TrainingWorkloadIndexRepository trainingIndexRepository;

  @InjectMocks private TrainerWorkloadServiceImpl service;

  @Test
  void acceptTrainerWorkloadShouldCreateMonthlyAggregateForAddAction() {
    TrainerWorkloadUpdateRequest request = addRequest(10L, LocalDate.of(2026, 7, 3), 60);
    when(trainingIndexRepository.findByTrainingId(10L)).thenReturn(Optional.empty());
    when(aggregateRepository.findByMonth("jane.doe", 2026, 7)).thenReturn(Optional.empty());

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<MonthlyWorkloadAggregate> aggregateCaptor =
        ArgumentCaptor.forClass(MonthlyWorkloadAggregate.class);
    verify(aggregateRepository).save(aggregateCaptor.capture());
    MonthlyWorkloadAggregate savedAggregate = aggregateCaptor.getValue();
    assertThat(savedAggregate.getTrainerUsername()).isEqualTo("jane.doe");
    assertThat(savedAggregate.getYear()).isEqualTo(2026);
    assertThat(savedAggregate.getMonth()).isEqualTo(7);
    assertThat(savedAggregate.getTotalDurationInMinutes()).isEqualTo(60);
    verify(trainingIndexRepository)
        .save(new TrainingWorkloadIndexEntry(10L, "jane.doe", 2026, 7, 60));
  }

  @Test
  void acceptTrainerWorkloadShouldIgnoreDuplicateAddAction() {
    TrainerWorkloadUpdateRequest request = addRequest(10L, LocalDate.of(2026, 7, 3), 60);
    when(trainingIndexRepository.findByTrainingId(10L))
        .thenReturn(Optional.of(new TrainingWorkloadIndexEntry(10L, "jane.doe", 2026, 7, 60)));

    service.acceptTrainerWorkload(request);

    verify(trainingIndexRepository).findByTrainingId(10L);
  }

  @Test
  void acceptTrainerWorkloadShouldReverseMonthlyAggregateForDeleteAction() {
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    MonthlyWorkloadAggregate existingAggregate = aggregate(2026, 7, 90);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(new TrainingWorkloadIndexEntry(11L, "jane.doe", 2026, 7, 60)));
    when(aggregateRepository.findByMonth("jane.doe", 2026, 7))
        .thenReturn(Optional.of(existingAggregate));

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<MonthlyWorkloadAggregate> aggregateCaptor =
        ArgumentCaptor.forClass(MonthlyWorkloadAggregate.class);
    verify(aggregateRepository).save(aggregateCaptor.capture());
    assertThat(aggregateCaptor.getValue().getTotalDurationInMinutes()).isEqualTo(30);
    verify(trainingIndexRepository).delete(11L);
  }

  @Test
  void acceptTrainerWorkloadShouldRejectDeleteForUnknownTrainingId() {
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(BusinessRuleViolationException.class)
        .hasMessageContaining("Training id was not processed");
  }

  @Test
  void getMonthlySummaryShouldGroupByYearAndOrderMonths() {
    MonthlyWorkloadAggregate july = aggregate(2026, 7, 120);
    MonthlyWorkloadAggregate june = aggregate(2026, 6, 90);
    MonthlyWorkloadAggregate january = aggregate(2025, 1, 30);
    when(aggregateRepository.findAllByTrainerUsername("jane.doe"))
        .thenReturn(List.of(july, june, january));

    TrainerMonthlySummaryResponse summary = service.getMonthlySummary("jane.doe");

    assertThat(summary.trainerUsername()).isEqualTo("jane.doe");
    assertThat(summary.years()).hasSize(2);
    assertThat(summary.years().get(0).year()).isEqualTo(2025);
    assertThat(summary.years().get(1).year()).isEqualTo(2026);
    assertThat(summary.years().get(1).months())
        .extracting(TrainerMonthlySummaryResponse.MonthSummary::month)
        .containsExactly(6, 7);
  }

  private TrainerWorkloadUpdateRequest addRequest(
      long trainingId, LocalDate trainingDate, int duration) {
    return new TrainerWorkloadUpdateRequest(
        "jane.doe",
        "Jane",
        "Doe",
        true,
        trainingDate,
        duration,
        ActionType.ADD,
        trainingId);
  }

  private MonthlyWorkloadAggregate aggregate(int year, int month, int duration) {
    MonthlyWorkloadAggregate aggregate = new MonthlyWorkloadAggregate();
    aggregate.setTrainerUsername("jane.doe");
    aggregate.setTrainerFirstName("Jane");
    aggregate.setTrainerLastName("Doe");
    aggregate.setTrainerActive(true);
    aggregate.setYear(year);
    aggregate.setMonth(month);
    aggregate.setTotalDurationInMinutes(duration);
    return aggregate;
  }
}
