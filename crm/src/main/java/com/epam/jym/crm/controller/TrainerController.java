package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.facade.CrmFacade;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/trainers")
@RequiredArgsConstructor
public class TrainerController {

  private final CrmFacade crmFacade;

  @PostMapping
  @ResponseStatus(HttpStatus.OK)
  public CredentialsDto register(@Valid @RequestBody TrainerCreateDto request) {
    return crmFacade.createTrainer(request);
  }

  @PatchMapping("/change-status")
  @ResponseStatus(HttpStatus.OK)
  public void updateStatus(
      @Valid @RequestParam String username,
      @RequestHeader("user-credentials") String userCredentials) {
    crmFacade.changeUserStatus(parse(userCredentials), username);
  }
}
