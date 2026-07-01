package com.epam.jym.crm.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.epam.jym.crm.service.JwtService;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

@ExtendWith(MockitoExtension.class)
class JwtAuthenticationFilterTest {

  @Mock private JwtService jwtService;
  @Mock private UserDetailsService userDetailsService;

  @InjectMocks private JwtAuthenticationFilter jwtAuthenticationFilter;

  @AfterEach
  void clearSecurityContext() {
    SecurityContextHolder.clearContext();
  }

  @Test
  void doFilterShouldAuthenticateWhenBearerTokenIsValid() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/trainees");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();
    UserDetails userDetails = User.withUsername("john.doe").password("password").roles("USER").build();
    when(jwtService.extractUsername("valid.token")).thenReturn("john.doe");
    when(userDetailsService.loadUserByUsername("john.doe")).thenReturn(userDetails);
    when(jwtService.isValid("valid.token", userDetails)).thenReturn(true);

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    var authentication = SecurityContextHolder.getContext().getAuthentication();
    assertThat(authentication).isNotNull();
    assertThat(authentication.getName()).isEqualTo("john.doe");
    verify(jwtService).extractUsername("valid.token");
    verify(userDetailsService).loadUserByUsername("john.doe");
    verify(jwtService).isValid("valid.token", userDetails);
  }

  @Test
  void doFilterShouldNotAuthenticateWhenBearerTokenIsInvalid() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/trainees");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer invalid.token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();
    when(jwtService.extractUsername("invalid.token")).thenThrow(new IllegalArgumentException("bad token"));

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verify(jwtService).extractUsername("invalid.token");
  }

  @Test
  void doFilterShouldSkipWhenAuthorizationHeaderIsMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/trainees");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    verifyNoInteractions(jwtService, userDetailsService);
  }

  @Test
  void doFilterShouldSkipWhenSecurityContextAlreadyHasAuthentication() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/trainees");
    request.addHeader(HttpHeaders.AUTHORIZATION, "Bearer valid.token");
    MockHttpServletResponse response = new MockHttpServletResponse();
    MockFilterChain filterChain = new MockFilterChain();
    SecurityContextHolder.getContext()
        .setAuthentication(
            new UsernamePasswordAuthenticationToken("existing.user", null, List.of()));

    jwtAuthenticationFilter.doFilter(request, response, filterChain);

    assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    assertThat(SecurityContextHolder.getContext().getAuthentication().getName())
        .isEqualTo("existing.user");
    verifyNoInteractions(jwtService, userDetailsService);
  }
}
