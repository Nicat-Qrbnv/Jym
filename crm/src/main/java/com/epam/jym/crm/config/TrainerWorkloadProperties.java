package com.epam.jym.crm.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "services.trainer-workload")
public record TrainerWorkloadProperties(String baseUrl) {}
