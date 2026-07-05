package com.epam.jym.trainerworkload.service.impl;

import com.epam.jym.trainerworkload.domain.MonthlyWorkloadAggregate;
import com.epam.jym.trainerworkload.domain.TrainingWorkloadIndexEntry;
import com.epam.jym.trainerworkload.dto.ActionType;
import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse;
import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse.MonthSummary;
import com.epam.jym.trainerworkload.dto.TrainerMonthlySummaryResponse.YearSummary;
import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;
import com.epam.jym.trainerworkload.exception.BusinessRuleViolationException;
import com.epam.jym.trainerworkload.exception.InvalidRequestException;
import com.epam.jym.trainerworkload.repository.TrainerWorkloadAggregateRepository;
import com.epam.jym.trainerworkload.repository.TrainingWorkloadIndexRepository;
import com.epam.jym.trainerworkload.service.TrainerWorkloadService;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TrainerWorkloadServiceImpl implements TrainerWorkloadService {

  private final TrainerWorkloadAggregateRepository aggregateRepository;
  private final TrainingWorkloadIndexRepository trainingIndexRepository;

  @Override
  public void acceptTrainerWorkload(TrainerWorkloadUpdateRequest request) {
    if (request == null) {
      throw new InvalidRequestException("request must not be null");
    }
    if (request.actionType() == ActionType.ADD) {
      processAdd(request);
      return;
    }
    processDelete(request);
  }

  @Override
  public TrainerMonthlySummaryResponse getMonthlySummary(String trainerUsername) {
    if (trainerUsername == null || trainerUsername.isBlank()) {
      throw new InvalidRequestException("trainerUsername must not be blank");
    }
    List<MonthlyWorkloadAggregate> aggregates =
        aggregateRepository.findAllByTrainerUsername(trainerUsername).stream()
            .sorted(
                Comparator.comparingInt(MonthlyWorkloadAggregate::getYear)
                    .thenComparingInt(MonthlyWorkloadAggregate::getMonth))
            .toList();

    if (aggregates.isEmpty()) {
      return new TrainerMonthlySummaryResponse(trainerUsername, null, null, null, List.of());
    }

    MonthlyWorkloadAggregate profileSource = aggregates.getFirst();
    return new TrainerMonthlySummaryResponse(
        profileSource.getTrainerUsername(),
        profileSource.getTrainerFirstName(),
        profileSource.getTrainerLastName(),
        profileSource.isTrainerActive(),
        toYearSummaries(aggregates));
  }

  private void processAdd(TrainerWorkloadUpdateRequest request) {
    long trainingId = request.trainingId();
    if (trainingIndexRepository.findByTrainingId(trainingId).isPresent()) {
      log.info("Skipping duplicate ADD workload update for trainingId={}", trainingId);
      return;
    }
    int year = request.trainingDate().getYear();
    int month = request.trainingDate().getMonthValue();
    MonthlyWorkloadAggregate aggregate =
        aggregateRepository
            .findByMonth(request.trainerUsername(), year, month)
            .orElseGet(() -> createEmptyAggregate(request, year, month));
    updateTrainerProfile(aggregate, request);
    aggregate.setTotalDurationInMinutes(
        aggregate.getTotalDurationInMinutes() + request.durationInMinutes());
    aggregateRepository.save(aggregate);
    trainingIndexRepository.save(
        new TrainingWorkloadIndexEntry(
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
    if (!indexEntry.trainerUsername().equals(request.trainerUsername())) {
      throw new BusinessRuleViolationException(
          "Cannot reverse workload. Training id belongs to another trainer: " + trainingId);
    }
    MonthlyWorkloadAggregate aggregate =
        aggregateRepository
            .findByMonth(indexEntry.trainerUsername(), indexEntry.year(), indexEntry.month())
            .orElseThrow(
                () ->
                    new BusinessRuleViolationException(
                        "Cannot reverse workload. Monthly aggregate is missing for training id: "
                            + trainingId));

    int updatedDuration = aggregate.getTotalDurationInMinutes() - indexEntry.durationInMinutes();
    if (updatedDuration < 0) {
      throw new BusinessRuleViolationException(
          "Cannot reverse workload. Total duration would become negative for training id: "
              + trainingId);
    }
    if (updatedDuration == 0) {
      aggregateRepository.delete(
          indexEntry.trainerUsername(), indexEntry.year(), indexEntry.month());
    } else {
      updateTrainerProfile(aggregate, request);
      aggregate.setTotalDurationInMinutes(updatedDuration);
      aggregateRepository.save(aggregate);
    }
    trainingIndexRepository.delete(trainingId);
  }

  private MonthlyWorkloadAggregate createEmptyAggregate(
      TrainerWorkloadUpdateRequest request, int year, int month) {
    MonthlyWorkloadAggregate aggregate = new MonthlyWorkloadAggregate();
    aggregate.setTrainerUsername(request.trainerUsername());
    aggregate.setTrainerFirstName(request.trainerFirstName());
    aggregate.setTrainerLastName(request.trainerLastName());
    aggregate.setTrainerActive(request.trainerActive());
    aggregate.setYear(year);
    aggregate.setMonth(month);
    aggregate.setTotalDurationInMinutes(0);
    return aggregate;
  }

  private void updateTrainerProfile(
      MonthlyWorkloadAggregate aggregate, TrainerWorkloadUpdateRequest request) {
    aggregate.setTrainerFirstName(request.trainerFirstName());
    aggregate.setTrainerLastName(request.trainerLastName());
    aggregate.setTrainerActive(request.trainerActive());
  }

  private List<YearSummary> toYearSummaries(List<MonthlyWorkloadAggregate> aggregates) {
    Map<Integer, List<MonthSummary>> yearToMonths = new TreeMap<>();
    for (MonthlyWorkloadAggregate aggregate : aggregates) {
      yearToMonths
          .computeIfAbsent(aggregate.getYear(), year -> new ArrayList<>())
          .add(new MonthSummary(aggregate.getMonth(), aggregate.getTotalDurationInMinutes()));
    }
    return yearToMonths.entrySet().stream()
        .map(
            entry ->
                new YearSummary(
                    entry.getKey(),
                    entry.getValue().stream()
                        .sorted(Comparator.comparingInt(MonthSummary::month))
                        .toList()))
        .toList();
  }
}
