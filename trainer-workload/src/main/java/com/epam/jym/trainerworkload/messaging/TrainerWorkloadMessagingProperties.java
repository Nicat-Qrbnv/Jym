package com.epam.jym.trainerworkload.messaging;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "messaging.trainer-workload")
public record TrainerWorkloadMessagingProperties(
    @NotBlank String queue, @NotBlank String dlq, @Min(2) int maxDeliveryAttempts) {}
