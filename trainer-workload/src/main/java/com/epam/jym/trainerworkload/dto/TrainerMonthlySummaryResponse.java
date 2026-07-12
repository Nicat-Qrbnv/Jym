package com.epam.jym.trainerworkload.dto;

import java.util.List;

public record TrainerMonthlySummaryResponse(
    String trainerUsername,
    String trainerFirstName,
    String trainerLastName,
    Boolean trainerActive,
    List<YearSummary> years) {

  public record YearSummary(int year, List<MonthSummary> months) {}

  public record MonthSummary(int month, int totalDurationInMinutes) {}
}
