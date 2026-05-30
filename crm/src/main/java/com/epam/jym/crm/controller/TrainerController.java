package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.dto.trainer.TrainerCreateDto;
import com.epam.jym.crm.dto.trainer.TrainerProfileDto;
import com.epam.jym.crm.dto.trainer.TrainerUpdateDto;
import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.facade.CrmFacade;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public TrainerProfileDto getProfile(
      @NotBlank @RequestParam String username,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.getTrainerProfile(parse(userCredentials), username);
  }

  @PutMapping
  @ResponseStatus(HttpStatus.OK)
  public TrainerProfileDto updateProfile(
      @RequestParam @NotBlank @Size(max = 310) String username,
      @Valid @RequestBody TrainerUpdateDto request,
      @RequestHeader("Authorization") String userCredentials) {
    return crmFacade.updateTrainerProfile(parse(userCredentials), username, request);
  }

  @PatchMapping("/change-status")
  @ResponseStatus(HttpStatus.OK)
  public void updateStatus(
      @Valid @RequestParam String username,
      @RequestHeader("Authorization") String userCredentials) {
    crmFacade.changeUserStatus(parse(userCredentials), username);
  }
}
