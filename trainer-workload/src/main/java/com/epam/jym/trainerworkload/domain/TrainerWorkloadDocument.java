package com.epam.jym.trainerworkload.domain;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "trainer_workloads")
@CompoundIndex(name = "firstName_lastName_idx", def = "{'firstName': 1, 'lastName': 1}")
public class TrainerWorkloadDocument {

  @Id private String username;
  private String firstName;
  private String lastName;
  private Boolean status;
  private List<YearSummary> years = new ArrayList<>();

  @Getter
  @Setter
  @NoArgsConstructor
  public static class YearSummary {

    private int year;
    private List<MonthSummary> months = new ArrayList<>();

    public YearSummary(int year) {
      this.year = year;
    }
  }

  @Getter
  @Setter
  @NoArgsConstructor
  public static class MonthSummary {

    private int month;
    private int totalDurationInMinutes;

    public MonthSummary(int month, int totalDurationInMinutes) {
      this.month = month;
      this.totalDurationInMinutes = totalDurationInMinutes;
    }
  }
}
