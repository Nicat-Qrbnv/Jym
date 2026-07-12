package com.epam.jym.trainerworkload;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class TrainerWorkloadApplication {

  static void main(String[] args) {
    SpringApplication.run(TrainerWorkloadApplication.class, args);
  }
}
