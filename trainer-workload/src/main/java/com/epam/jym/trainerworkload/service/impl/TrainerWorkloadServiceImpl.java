package com.epam.jym.trainerworkload.service.impl;

import com.epam.jym.trainerworkload.domain.TrainerWorkloadDocument;
import com.epam.jym.trainerworkload.domain.TrainerWorkloadDocument.MonthSummary;
import com.epam.jym.trainerworkload.domain.TrainerWorkloadDocument.YearSummary;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.exception.BusinessRuleViolationException;
import com.epam.jym.trainerworkload.exception.InvalidRequestException;
import com.epam.jym.trainerworkload.repository.TrainerWorkloadDocumentRepository;
import com.epam.jym.trainerworkload.repository.TrainingWorkloadIndexRepository;
import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

  private final TrainerWorkloadDocumentRepository workloadRepository;
  private final TrainingWorkloadIndexRepository trainingIndexRepository;

  @Override
  public void acceptTrainerWorkload(TrainerWorkloadUpdateRequest request) {
    if (request == null) {
      throw new InvalidRequestException("request must not be null");
    }
    trainingIndexRepository.runWithTrainingLock(
        request.trainingId(),
        () -> {
          switch (request.actionType()) {
            case ADD -> processAdd(request);
            case DELETE -> processDelete(request);
            case null -> throw new InvalidRequestException("actionType must not be null");
          }
        });
  }

  @Override
  public void acceptTrainerWorkload(List<TrainerWorkloadUpdateRequest> requests) {
    if (requests == null) {
      throw new InvalidRequestException("requests must not be null");
    }
    requests.forEach(this::acceptTrainerWorkload);
  }

  @Override
  public TrainerMonthlySummaryResponse getMonthlySummary(String trainerUsername) {
    if (trainerUsername == null || trainerUsername.isBlank()) {
      throw new InvalidRequestException("trainerUsername must not be blank");
    }
    return workloadRepository.findByUsername(trainerUsername)
        .map(this::toResponse)
        .orElse(new TrainerMonthlySummaryResponse(trainerUsername, null, null, null, List.of()));
  }

  private void processAdd(TrainerWorkloadUpdateRequest request) {
    long trainingId = request.trainingId();
    TrainingWorkloadIndexEntry existingEntry =
        trainingIndexRepository.findByTrainingId(trainingId).orElse(null);
    if (existingEntry != null) {
      if (existingEntry.isDeleted()) {
        log.info("Skipping stale ADD workload update for deleted trainingId={}", trainingId);
      } else {
        log.info("Skipping duplicate ADD workload update for trainingId={}", trainingId);
      }
      return;
    }
    int year = request.trainingDate().getYear();
    int month = request.trainingDate().getMonthValue();

    TrainerWorkloadDocument doc = workloadRepository.findByUsername(request.trainerUsername())
        .orElseGet(() -> createDocument(request));
    updateProfile(doc, request);
    addDuration(doc, year, month, request.durationInMinutes());
    workloadRepository.save(doc);

    trainingIndexRepository.save(
        TrainingWorkloadIndexEntry.active(
            trainingId, request.trainerUsername(), year, month, request.durationInMinutes()));
  }

  private void processDelete(TrainerWorkloadUpdateRequest request) {
    long trainingId = request.trainingId();
    TrainingWorkloadIndexEntry indexEntry =
        trainingIndexRepository
            .findByTrainingId(trainingId)
            .orElseThrow(
                () ->
                    new BusinessRuleViolationException(
                        "Cannot reverse workload. Training id was not processed: " + trainingId));
    if (indexEntry.isDeleted()) {
      log.info("Skipping duplicate DELETE workload update for trainingId={}", trainingId);
      return;
    }
    if (!indexEntry.trainerUsername().equals(request.trainerUsername())) {
      throw new BusinessRuleViolationException(
          "Cannot reverse workload. Training id belongs to another trainer: " + trainingId);
    }
    TrainerWorkloadDocument doc =
        workloadRepository
            .findByUsername(indexEntry.trainerUsername())
            .orElseThrow(
                () ->
                    new BusinessRuleViolationException(
                        "Cannot reverse workload. Workload document is missing for training id: "
                            + trainingId));

    updateProfile(doc, request);
    subtractDuration(doc, indexEntry.year(), indexEntry.month(), indexEntry.durationInMinutes(),
        trainingId);
    workloadRepository.save(doc);
    trainingIndexRepository.save(indexEntry.asDeleted());
  }

  private void addDuration(TrainerWorkloadDocument doc, int year, int month, int duration) {
    YearSummary yearSummary = findOrCreateYear(doc, year);
    MonthSummary monthSummary = findOrCreateMonth(yearSummary, month);
    monthSummary.setTotalDurationInMinutes(monthSummary.getTotalDurationInMinutes() + duration);
  }

  private void subtractDuration(TrainerWorkloadDocument doc, int year, int month, int duration,
      long trainingId) {
    YearSummary yearSummary = doc.getYears().stream()
        .filter(y -> y.getYear() == year)
        .findFirst()
        .orElseThrow(() -> new BusinessRuleViolationException(
            "Cannot reverse workload. Year entry missing for training id: " + trainingId));
    MonthSummary monthSummary = yearSummary.getMonths().stream()
        .filter(m -> m.getMonth() == month)
        .findFirst()
        .orElseThrow(() -> new BusinessRuleViolationException(
            "Cannot reverse workload. Month entry missing for training id: " + trainingId));

    int updated = monthSummary.getTotalDurationInMinutes() - duration;
    if (updated < 0) {
      throw new BusinessRuleViolationException(
          "Cannot reverse workload. Total duration would become negative for training id: "
              + trainingId);
    }
    if (updated == 0) {
      yearSummary.getMonths().remove(monthSummary);
      if (yearSummary.getMonths().isEmpty()) {
        doc.getYears().remove(yearSummary);
      }
    } else {
      monthSummary.setTotalDurationInMinutes(updated);
    }
  }

  private YearSummary findOrCreateYear(TrainerWorkloadDocument doc, int year) {
    return doc.getYears().stream()
        .filter(y -> y.getYear() == year)
        .findFirst()
        .orElseGet(() -> {
          YearSummary newYear = new YearSummary(year);
          doc.getYears().add(newYear);
          return newYear;
        });
  }

  private MonthSummary findOrCreateMonth(YearSummary yearSummary, int month) {
    return yearSummary.getMonths().stream()
        .filter(m -> m.getMonth() == month)
        .findFirst()
        .orElseGet(() -> {
          MonthSummary newMonth = new MonthSummary(month, 0);
          yearSummary.getMonths().add(newMonth);
          return newMonth;
        });
  }

  private TrainerWorkloadDocument createDocument(TrainerWorkloadUpdateRequest request) {
    TrainerWorkloadDocument doc = new TrainerWorkloadDocument();
    doc.setUsername(request.trainerUsername());
    doc.setFirstName(request.trainerFirstName());
    doc.setLastName(request.trainerLastName());
    doc.setStatus(request.trainerActive());
    doc.setYears(new ArrayList<>());
    return doc;
  }

  private void updateProfile(TrainerWorkloadDocument doc, TrainerWorkloadUpdateRequest request) {
    doc.setFirstName(request.trainerFirstName());
    doc.setLastName(request.trainerLastName());
    doc.setStatus(request.trainerActive());
  }

  private TrainerMonthlySummaryResponse toResponse(TrainerWorkloadDocument doc) {
    List<TrainerMonthlySummaryResponse.YearSummary> years = doc.getYears().stream()
        .sorted(Comparator.comparingInt(YearSummary::getYear))
        .map(y -> new TrainerMonthlySummaryResponse.YearSummary(
            y.getYear(),
            y.getMonths().stream()
                .sorted(Comparator.comparingInt(MonthSummary::getMonth))
                .map(m -> new TrainerMonthlySummaryResponse.MonthSummary(
                    m.getMonth(), m.getTotalDurationInMinutes()))
                .toList()))
        .toList();
    return new TrainerMonthlySummaryResponse(
        doc.getUsername(),
        doc.getFirstName(),
        doc.getLastName(),
        doc.getStatus(),
        years);
  }
}
