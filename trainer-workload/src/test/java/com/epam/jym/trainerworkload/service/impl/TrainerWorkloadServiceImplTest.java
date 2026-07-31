package com.epam.jym.trainerworkload.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.trainerworkload.domain.TrainerWorkloadDocument;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexState;
import com.epam.jym.trainerworkload.dto.ActionType;
import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.exception.BusinessRuleViolationException;
import com.epam.jym.trainerworkload.exception.InvalidRequestException;
import com.epam.jym.trainerworkload.repository.TrainerWorkloadDocumentRepository;
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

  @Mock private TrainerWorkloadDocumentRepository workloadRepository;
  @Mock private TrainingWorkloadIndexRepository trainingIndexRepository;

  @InjectMocks private TrainerWorkloadServiceImpl service;

  @Test
  void acceptTrainerWorkloadShouldCreateMonthlyAggregateForAddAction() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request = addRequest(10L, LocalDate.of(2026, 7, 3), 60);
    when(trainingIndexRepository.findByTrainingId(10L)).thenReturn(Optional.empty());
    when(workloadRepository.findByUsername("jane.doe")).thenReturn(Optional.empty());

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<TrainerWorkloadDocument> docCaptor =
        ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
    verify(workloadRepository).save(docCaptor.capture());
    TrainerWorkloadDocument saved = docCaptor.getValue();
    assertThat(saved.getUsername()).isEqualTo("jane.doe");
    assertThat(saved.getYears()).hasSize(1);
    assertThat(saved.getYears().getFirst().getYear()).isEqualTo(2026);
    assertThat(saved.getYears().getFirst().getMonths()).hasSize(1);
    assertThat(saved.getYears().getFirst().getMonths().getFirst().getMonth()).isEqualTo(7);
    assertThat(saved.getYears().getFirst().getMonths().getFirst().getTotalDurationInMinutes())
        .isEqualTo(60);
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
    verifyNoInteractions(workloadRepository);
  }

  @Test
  void acceptTrainerWorkloadShouldIgnoreStaleAddAfterDelete() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request = addRequest(10L, LocalDate.of(2026, 7, 3), 60);
    when(trainingIndexRepository.findByTrainingId(10L))
        .thenReturn(
            Optional.of(
                new TrainingWorkloadIndexEntry(
                    10L, "jane.doe", 2026, 7, 60, TrainingWorkloadIndexState.DELETED)));

    service.acceptTrainerWorkload(request);

    verify(trainingIndexRepository).findByTrainingId(10L);
    verifyNoInteractions(workloadRepository);
  }

  @Test
  void acceptTrainerWorkloadShouldReverseMonthlyAggregateForDeleteAction() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(workloadRepository.findByUsername("jane.doe"))
        .thenReturn(Optional.of(documentWithMonth(7, 90)));

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<TrainerWorkloadDocument> docCaptor =
        ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
    verify(workloadRepository).save(docCaptor.capture());
    assertThat(
            docCaptor
                .getValue()
                .getYears()
                .getFirst()
                .getMonths()
                .getFirst()
                .getTotalDurationInMinutes())
        .isEqualTo(30);
    verify(trainingIndexRepository)
        .save(
            new TrainingWorkloadIndexEntry(
                11L, "jane.doe", 2026, 7, 60, TrainingWorkloadIndexState.DELETED));
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
                    11L, "jane.doe", 2026, 7, 60, TrainingWorkloadIndexState.DELETED)));

    service.acceptTrainerWorkload(request);

    verifyNoInteractions(workloadRepository);
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
    TrainerWorkloadDocument existingDoc = documentWithMonth(8, 75);
    existingDoc.setFirstName("Old");
    existingDoc.setLastName("Name");
    existingDoc.setStatus(false);
    when(trainingIndexRepository.findByTrainingId(20L)).thenReturn(Optional.empty());
    when(workloadRepository.findByUsername("jane.doe")).thenReturn(Optional.of(existingDoc));

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<TrainerWorkloadDocument> docCaptor =
        ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
    verify(workloadRepository).save(docCaptor.capture());
    TrainerWorkloadDocument saved = docCaptor.getValue();
    assertThat(saved.getYears().getFirst().getMonths().getFirst().getTotalDurationInMinutes())
        .isEqualTo(120);
    assertThat(saved.getFirstName()).isEqualTo("Jane");
    assertThat(saved.getLastName()).isEqualTo("Doe");
    assertThat(saved.getStatus()).isTrue();
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
  void acceptTrainerWorkloadShouldRejectDeleteWhenWorkloadDocumentIsMissing() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(workloadRepository.findByUsername("jane.doe")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(BusinessRuleViolationException.class)
        .hasMessageContaining("Workload document is missing");
  }

  @Test
  void acceptTrainerWorkloadShouldRejectDeleteThatWouldMakeDurationNegative() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(workloadRepository.findByUsername("jane.doe"))
        .thenReturn(Optional.of(documentWithMonth(7, 30)));

    assertThatThrownBy(() -> service.acceptTrainerWorkload(request))
        .isInstanceOf(BusinessRuleViolationException.class)
        .hasMessageContaining("would become negative");
  }

  @Test
  void acceptTrainerWorkloadShouldRemoveMonthEntryWhenDurationBecomesZero() {
    stubTrainingLock();
    TrainerWorkloadUpdateRequest request =
        new TrainerWorkloadUpdateRequest(
            "jane.doe", "Jane", "Doe", true, LocalDate.of(2026, 7, 4), 60, ActionType.DELETE, 11L);
    when(trainingIndexRepository.findByTrainingId(11L))
        .thenReturn(Optional.of(TrainingWorkloadIndexEntry.active(11L, "jane.doe", 2026, 7, 60)));
    when(workloadRepository.findByUsername("jane.doe"))
        .thenReturn(Optional.of(documentWithMonth(7, 60)));

    service.acceptTrainerWorkload(request);

    ArgumentCaptor<TrainerWorkloadDocument> docCaptor =
        ArgumentCaptor.forClass(TrainerWorkloadDocument.class);
    verify(workloadRepository).save(docCaptor.capture());
    assertThat(docCaptor.getValue().getYears()).isEmpty();
    verify(trainingIndexRepository)
        .save(
            new TrainingWorkloadIndexEntry(
                11L, "jane.doe", 2026, 7, 60, TrainingWorkloadIndexState.DELETED));
  }

  @Test
  void getMonthlySummaryShouldGroupByYearAndOrderMonths() {
    TrainerWorkloadDocument doc = new TrainerWorkloadDocument();
    doc.setUsername("jane.doe");
    doc.setFirstName("Jane");
    doc.setLastName("Doe");
    doc.setStatus(true);
    TrainerWorkloadDocument.YearSummary year2025 = new TrainerWorkloadDocument.YearSummary(2025);
    year2025.getMonths().add(new TrainerWorkloadDocument.MonthSummary(1, 30));
    TrainerWorkloadDocument.YearSummary year2026 = new TrainerWorkloadDocument.YearSummary(2026);
    year2026.getMonths().add(new TrainerWorkloadDocument.MonthSummary(7, 120));
    year2026.getMonths().add(new TrainerWorkloadDocument.MonthSummary(6, 90));
    doc.getYears().add(year2026);
    doc.getYears().add(year2025);
    when(workloadRepository.findByUsername("jane.doe")).thenReturn(Optional.of(doc));

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
  void getMonthlySummaryShouldReturnEmptyResponseWhenNoDocumentExists() {
    when(workloadRepository.findByUsername("jane.doe")).thenReturn(Optional.empty());

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
        "jane.doe", "Jane", "Doe", true, trainingDate, duration, ActionType.ADD, trainingId);
  }

  private TrainerWorkloadDocument documentWithMonth(int month, int duration) {
    TrainerWorkloadDocument doc = new TrainerWorkloadDocument();
    doc.setUsername("jane.doe");
    doc.setFirstName("Jane");
    doc.setLastName("Doe");
    doc.setStatus(true);
    TrainerWorkloadDocument.MonthSummary monthSummary =
        new TrainerWorkloadDocument.MonthSummary(month, duration);
    TrainerWorkloadDocument.YearSummary yearSummary = new TrainerWorkloadDocument.YearSummary(2026);
    yearSummary.getMonths().add(monthSummary);
    doc.getYears().add(yearSummary);
    return doc;
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
