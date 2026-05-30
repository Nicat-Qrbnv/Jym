package com.epam.jym.crm.controller;

import static com.epam.jym.crm.util.CredentialsHeaderParser.parse;

import com.epam.jym.crm.facade.CrmFacade;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

  private final CrmFacade crmFacade;

  @GetMapping("/login")
  @ResponseStatus(HttpStatus.OK)
  public void login(@RequestHeader("Authorization") String userCredentials) {
    crmFacade.login(parse(userCredentials));
  }

  @PutMapping("/change-password")
  @ResponseStatus(HttpStatus.OK)
  public void changeLogin(
      @RequestHeader("Authorization") String userCredentials, @NotBlank String newPassword) {
    crmFacade.changeLogin(parse(userCredentials), newPassword);
  }
}
