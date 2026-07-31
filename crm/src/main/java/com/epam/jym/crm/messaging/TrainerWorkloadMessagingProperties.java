package com.epam.jym.crm.messaging;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "messaging.trainer-workload")
public record TrainerWorkloadMessagingProperties(@NotBlank String queue) {}
