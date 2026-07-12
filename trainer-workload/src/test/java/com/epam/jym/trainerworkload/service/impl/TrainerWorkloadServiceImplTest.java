package com.epam.jym.trainerworkload.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.trainerworkload.domain.MonthlyWorkloadAggregate;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexState;
import com.epam.jym.trainerworkload.dto.ActionType;
import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.exception.BusinessRuleViolationException;
import com.epam.jym.trainerworkload.exception.InvalidRequestException;
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
    stubTrainingLock();
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
        .save(TrainingWorkloadIndexEntry.active(10L, "jane.doe", 2026, 7, 60));
  }

  @Test
  void acceptTrainerWorkloadShouldIgnoreDuplicateAddAction() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request = addRequest(10L, LocalDate.of(2026, 7, 3), 60);
    when(trainingIndexRepository.findByTrainingId(10L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(10L, "jane.doe", 2026, 7, 60)));

    service.acceptTrainerWorkload(request);

    verify(trainingIndexRepository).findByTrainingId(10L);
    verifyNoInteractions(aggregateRepository);
  }

  @Test
  void acceptTrainerWorkloadShouldIgnoreStaleAddAfterDelete() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request = addRequest(10L, LocalDate.of(2026, 7, 3), 60);
    when(trainingIndexRepository.findByTrainingId(10L))
        .thenReturn(
            Optional.of(
                new TrainingWorkloadIndexEntry(
                    10L,
                    "jane.doe",
                    2026,
                    7,
                    60,
                    TrainingWorkloadIndexState.DELETED)));

    service.acceptTrainerWorkload(request);

    verify(trainingIndexRepository).findByTrainingId(10L);
    verifyNoInteractions(aggregateRepository);
  }

  @Test
  void acceptTrainerWorkloadShouldReverseMonthlyAggregateForDeleteAction() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    MonthlyWorkloadAggregate existingAggregate = aggregate(2026, 7, 90);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(aggregateRepository.findByMonth("jane.doe", 2026, 7))
        .thenReturn(Optional.of(existingAggregate));

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<MonthlyWorkloadAggregate> aggregateCaptor =
        ArgumentCaptor.forClass(MonthlyWorkloadAggregate.class);
    verify(aggregateRepository).save(aggregateCaptor.capture());
    assertThat(aggregateCaptor.getValue().getTotalDurationInMinutes()).isEqualTo(30);
    verify(trainingIndexRepository)
        .save(
            new TrainingWorkloadIndexEntry(
                11L,
                "jane.doe",
                2026,
                7,
                60,
                TrainingWorkloadIndexState.DELETED));
  }

  @Test
  void acceptTrainerWorkloadShouldRejectDeleteForUnknownTrainingId() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(BusinessRuleViolationException.class)
        .hasMessageContaining("Training id was not processed");
  }

  @Test
  void acceptTrainerWorkloadShouldIgnoreDuplicateDeleteAction() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(
            Optional.of(
                new TrainingWorkloadIndexEntry(
                    11L,
                    "jane.doe",
                    2026,
                    7,
                    60,
                    TrainingWorkloadIndexState.DELETED)));

    service.acceptTrainerWorkload(request);

    verifyNoInteractions(aggregateRepository);
  }

  @Test
  void acceptTrainerWorkloadShouldRejectNullRequest() {
    assertThatThrownBy(() -> service.acceptTrainerWorkload((TrainerWorkloadUpdateRequest) null))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessageContaining("request must not be null");
  }

  @Test
  void acceptTrainerWorkloadShouldRejectNullActionType() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, null, 11L);

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessageContaining("actionType must not be null");
  }

  @Test
  void acceptTrainerWorkloadShouldRejectNullBatch() {
    assertThatThrownBy(
            () -> service.acceptTrainerWorkload((List<TrainerWorkloadUpdateRequest>) null))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessageContaining("requests must not be null");
  }

  @Test
  void acceptTrainerWorkloadShouldUpdateExistingMonthlyAggregateForAddAction() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request = addRequest(20L, LocalDate.of(2026, 8, 3), 45);
    MonthlyWorkloadAggregate existingAggregate = aggregate(2026, 8, 75);
    existingAggregate.setTrainerFirstName("Old");
    existingAggregate.setTrainerLastName("Name");
    existingAggregate.setTrainerActive(false);
    when(trainingIndexRepository.findByTrainingId(20L)).thenReturn(Optional.empty());
    when(aggregateRepository.findByMonth("jane.doe", 2026, 8))
        .thenReturn(Optional.of(existingAggregate));

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<MonthlyWorkloadAggregate> aggregateCaptor =
        ArgumentCaptor.forClass(MonthlyWorkloadAggregate.class);
    verify(aggregateRepository).save(aggregateCaptor.capture());
    MonthlyWorkloadAggregate savedAggregate = aggregateCaptor.getValue();
    assertThat(savedAggregate.getTotalDurationInMinutes()).isEqualTo(120);
    assertThat(savedAggregate.getTrainerFirstName()).isEqualTo("Jane");
    assertThat(savedAggregate.getTrainerLastName()).isEqualTo("Doe");
    assertThat(savedAggregate.isTrainerActive()).isTrue();
  }

  @Test
  void acceptTrainerWorkloadShouldRejectDeleteWhenTrainingBelongsToAnotherTrainer() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "john.doe", 2026, 7, 60)));

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(BusinessRuleViolationException.class)
        .hasMessageContaining("belongs to another trainer");
  }

  @Test
  void acceptTrainerWorkloadShouldRejectDeleteWhenMonthlyAggregateIsMissing() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(aggregateRepository.findByMonth("jane.doe", 2026, 7)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(BusinessRuleViolationException.class)
        .hasMessageContaining("Monthly aggregate is missing");
  }

  @Test
  void acceptTrainerWorkloadShouldRejectDeleteThatWouldMakeDurationNegative() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(aggregateRepository.findByMonth("jane.doe", 2026, 7))
        .thenReturn(Optional.of(aggregate(2026, 7, 30)));

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(BusinessRuleViolationException.class)
        .hasMessageContaining("would become negative");
  }

  @Test
  void acceptTrainerWorkloadShouldDeleteMonthlyAggregateWhenDurationBecomesZero() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(aggregateRepository.findByMonth("jane.doe", 2026, 7))
        .thenReturn(Optional.of(aggregate(2026, 7, 60)));

    service.acceptTrainerWorkload(request);

    verify(aggregateRepository).delete("jane.doe", 2026, 7);
    verify(trainingIndexRepository)
        .save(
            new TrainingWorkloadIndexEntry(
                11L,
                "jane.doe",
                2026,
                7,
                60,
                TrainingWorkloadIndexState.DELETED));
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

  @Test
  void getMonthlySummaryShouldRejectBlankTrainerUsername() {
    assertThatThrownBy(() -> service.getMonthlySummary("   "))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessageContaining("trainerUsername must not be blank");
  }

  @Test
  void getMonthlySummaryShouldReturnEmptyResponseWhenNoAggregatesExist() {
    when(aggregateRepository.findAllByTrainerUsername("jane.doe")).thenReturn(List.of());

    TrainerMonthlySummaryResponse summary = service.getMonthlySummary("jane.doe");

    assertThat(summary.trainerUsername()).isEqualTo("jane.doe");
    assertThat(summary.trainerFirstName()).isNull();
    assertThat(summary.trainerLastName()).isNull();
    assertThat(summary.trainerActive()).isNull();
    assertThat(summary.years()).isEmpty();
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

  private void stubTrainingLock() {
    doAnswer(
            invocation -> {
              Runnable action = invocation.getArgument(1);
              action.run();
              return null;
            })
        .when(trainingIndexRepository)
        .runWithTrainingLock(anyLong(), any(Runnable.class));
  }
}
