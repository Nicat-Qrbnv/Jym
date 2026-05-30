package com.epam.jym.crm.auth;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthenticationAspect {

  private final AuthenticationService authenticationService;

  @Pointcut("@within(com.epam.jym.crm.auth.Authenticated)")
  public void authenticatedMethods() {}

  @Pointcut("@annotation(com.epam.jym.crm.auth.SkipAuthentication)")
  public void skipAuthenticationMethods() {}

  @Around("authenticatedMethods() && !skipAuthenticationMethods())")
  public Object authenticate(ProceedingJoinPoint joinPoint) throws Throwable {
    CredentialsDto credentials = findCredentials(joinPoint.getArgs());
    authenticationService.authenticate(credentials);
    return joinPoint.proceed();
  }

  private CredentialsDto findCredentials(Object[] args) {
    for (Object arg : args) {
      if (arg instanceof CredentialsDto credentials) {
        return credentials;
      }
    }
    throw new IllegalArgumentException("Credentials are required");
  }
}
