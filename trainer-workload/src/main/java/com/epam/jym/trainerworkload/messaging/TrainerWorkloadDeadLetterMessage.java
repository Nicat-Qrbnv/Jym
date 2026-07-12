package com.epam.jym.trainerworkload.messaging;

import com.epam.jym.trainerworkload.dto.TrainerWorkloadUpdateRequest;

public record TrainerWorkloadDeadLetterMessage(
    TrainerWorkloadUpdateRequest request,
    String traceId,
    int deliveryCount,
    String errorMessage) {}
