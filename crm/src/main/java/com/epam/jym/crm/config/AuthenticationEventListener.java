package com.epam.jym.crm.config;

import com.epam.jym.crm.service.BruteForceProtectionService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationFailureBadCredentialsEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuthenticationEventListener {

  private final BruteForceProtectionService bruteForceProtectionService;

  @EventListener
  public void onAuthenticationFailure(AuthenticationFailureBadCredentialsEvent event) {
    bruteForceProtectionService.registerFailure(event.getAuthentication().getName());
  }

  @EventListener
  public void onAuthenticationSuccess(AuthenticationSuccessEvent event) {
    bruteForceProtectionService.registerSuccess(event.getAuthentication().getName());
  }
}
