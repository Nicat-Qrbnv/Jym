package com.epam.jym.crm.actuator;

import com.epam.jym.crm.repository.SpecializationTrainerCount;
import com.epam.jym.crm.repository.TrainerRepository;
import com.epam.jym.crm.repository.UserRepository;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
import org.springframework.stereotype.Component;

@Component
@Endpoint(id = "crmMetrics")
public class CrmMetricsEndpoint {

  private final TrainerRepository trainerRepository;
  private final UserRepository userRepository;

  public CrmMetricsEndpoint(TrainerRepository trainerRepository, UserRepository userRepository) {
    this.trainerRepository = trainerRepository;
    this.userRepository = userRepository;
  }

  @ReadOperation
  public CrmMetrics metrics() {
    return new CrmMetrics(userRepository.countByIsActiveTrue(), trainersBySpecialization());
  }

  private Map<String, Long> trainersBySpecialization() {
    return trainerRepository.countActiveTrainersBySpecialization().stream()
        .collect(
            Collectors.toMap(
                SpecializationTrainerCount::getSpecializationType,
                SpecializationTrainerCount::getTrainerCount,
                (left, _) -> left,
                LinkedHashMap::new));
  }

  public record CrmMetrics(long activeUsers, Map<String, Long> trainersBySpecialization) {}
}
