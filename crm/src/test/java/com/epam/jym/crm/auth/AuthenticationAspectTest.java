package com.epam.jym.crm.auth;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.dto.auth.CredentialsDto;
import com.epam.jym.crm.service.AuthenticationService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AuthenticationAspectTest {

  @Mock private AuthenticationService authenticationService;

  @Mock private ProceedingJoinPoint joinPoint;

  @Test
  void authenticateShouldAuthenticateCredentialsAndProceedWithFacadeCall() throws Throwable {
    CredentialsDto credentials = new CredentialsDto("john.doe", "password");
    AuthenticationAspect aspect = new AuthenticationAspect(authenticationService);

    when(joinPoint.getArgs()).thenReturn(new Object[] {credentials});
    when(joinPoint.proceed()).thenReturn("result");

    Object result = aspect.authenticate(joinPoint);

    Assertions.assertThat(result).isEqualTo("result");
    verify(authenticationService).authenticate(credentials);
    verify(joinPoint).proceed();
  }

  @Test
  void authenticateShouldThrowWhenCredentialsArgumentIsMissing() {
    AuthenticationAspect aspect = new AuthenticationAspect(authenticationService);
    when(joinPoint.getArgs()).thenReturn(new Object[] {"not credentials"});

    Assertions.assertThatThrownBy(() -> aspect.authenticate(joinPoint))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("Credentials are required");
    verifyNoInteractions(authenticationService);
  }
}
