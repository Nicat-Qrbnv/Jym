package com.epam.jym.crm.logging;

import static com.epam.jym.crm.logging.TransactionLoggingConstants.TRANSACTION_ID_HEADER;
import static com.epam.jym.crm.logging.TransactionLoggingConstants.TRANSACTION_ID_MDC_KEY;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.util.UUID;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class TransactionLoggingFilterTest {

  private final TransactionLoggingFilter filter = new TransactionLoggingFilter();

  @Test
  void doFilterShouldGenerateTransactionIdWhenHeaderIsMissing() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/v1/trainees");
    MockHttpServletResponse response = new MockHttpServletResponse();

    filter.doFilter(request, response, new MockFilterChain());

    String transactionId = response.getHeader(TRANSACTION_ID_HEADER);
    Assertions.assertThat(transactionId).isNotBlank();
    Assertions.assertThatCode(() -> UUID.fromString(transactionId)).doesNotThrowAnyException();
    Assertions.assertThat(MDC.get(TRANSACTION_ID_MDC_KEY)).isNull();
  }

  @Test
  void doFilterShouldReuseIncomingTransactionId() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/trainings");
    MockHttpServletResponse response = new MockHttpServletResponse();
    request.addHeader(TRANSACTION_ID_HEADER, "incoming-transaction-id");

    filter.doFilter(request, response, new MockFilterChain());

    Assertions.assertThat(response.getHeader(TRANSACTION_ID_HEADER))
        .isEqualTo("incoming-transaction-id");
    Assertions.assertThat(MDC.get(TRANSACTION_ID_MDC_KEY)).isNull();
  }

  @Test
  void doFilterShouldPreserveResponseBodyAfterLogging() throws Exception {
    MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/trainees");
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
