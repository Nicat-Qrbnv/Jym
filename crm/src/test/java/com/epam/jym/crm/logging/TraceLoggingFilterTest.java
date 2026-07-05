package com.epam.jym.crm.logging;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_HEADER;
import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_MDC_KEY;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class TraceLoggingFilterTest {

  private final TraceLoggingFilter filter = new TraceLoggingFilter();

  @Test
  void doFilterShouldNotInventTraceIdWhenHeaderIsMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/v1/trainees");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, new MockFilterChain());

    Assertions.assertThat(response.getHeader(TRACE_ID_HEADER)).isNull();
    Assertions.assertThat(MDC.get(TRACE_ID_MDC_KEY)).isNull();
  }

  @Test
  void doFilterShouldReuseIncomingTraceId() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/v1/trainings");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader(TRACE_ID_HEADER, "incoming-trace-id");

    filter.doFilter(request, response, new MockFilterChain());

    Assertions.assertThat(response.getHeader(TRACE_ID_HEADER)).isEqualTo("incoming-trace-id");
    Assertions.assertThat(MDC.get(TRACE_ID_MDC_KEY)).isNull();
  }

  @Test
  void doFilterShouldPreserveResponseBodyAfterLogging() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/v1/trainees");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.setContentType("application/json");
    request.setContent(
        """
        {"username":"john.doe","password":"secret"}
        """
            .getBytes());
    FilterChain filterChain =
        (ServletRequest servletRequest, ServletResponse servletResponse) -> {
          servletRequest.getReader().readLine();
          servletResponse.setContentType("application/json");
          servletResponse.getWriter().write("{\"result\":\"created\"}");
        };

    filter.doFilter(request, response, filterChain);

    Assertions.assertThat(response.getContentAsString()).isEqualTo("{\"result\":\"created\"}");
  }
}
