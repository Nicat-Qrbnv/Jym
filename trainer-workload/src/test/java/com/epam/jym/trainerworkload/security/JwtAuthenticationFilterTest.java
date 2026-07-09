package com.epam.jym.trainerworkload.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.epam.jym.jwthandler.service.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;

  @InjectMocks private JwtAuthenticationFilter filter;

  @Test
  void shouldRejectProtectedRequestWithoutBearerToken() throws Exception {
    MockHttpServletRequest request =
        new MockHttpServletRequest("POST", "/v1/trainer-workloads");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(401);
  }

  @Test
  void shouldAllowProtectedRequestWithValidBearerToken() throws Exception {
    MockHttpServletRequest request =
        new MockHttpServletRequest("POST", "/v1/trainer-workloads");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid-token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain chain = new MockFilterChain();
    when(jwtService.isValid("valid-token")).thenReturn(true);
    when(jwtService.extractUsername("valid-token")).thenReturn("john.doe");

    filter.doFilter(request, response, chain);

    assertThat(response.getStatus()).isEqualTo(200);
    assertThat(chain.getRequest()).isNotNull();
  }
}
