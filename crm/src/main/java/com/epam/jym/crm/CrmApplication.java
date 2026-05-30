package com.epam.jym.crm;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CrmApplication {

  static void main(String[] args) {
    SpringApplication.run(CrmApplication.class, args);
    // TODO: apply authorization
  }
}
