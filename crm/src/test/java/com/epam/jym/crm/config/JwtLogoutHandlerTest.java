package com.epam.jym.crm.config;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import com.epam.jym.crm.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtLogoutHandlerTest {

  @Mock private JwtService jwtService;
  @InjectMocks private JwtLogoutHandler jwtLogoutHandler;

  @Test
  void logoutShouldRevokeBearerToken() {
    MockHttpServletRequest request = new MockHttpServletRequest();
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer test.token.value");

    jwtLogoutHandler.logout(request, new MockHttpServletResponse(), null);

    verify(jwtService).revokeToken("test.token.value");
  }

  @Test
  void logoutShouldSkipWhenAuthorizationHeaderIsMissing() {
    MockHttpServletRequest request = new MockHttpServletRequest();

    jwtLogoutHandler.logout(request, new MockHttpServletResponse(), null);

    verifyNoInteractions(jwtService);
  }
}
