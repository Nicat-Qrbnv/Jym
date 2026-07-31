package com.epam.jym.crm.messaging;

import static com.epam.jym.crm.logging.TraceLoggingConstants.TRACE_ID_HEADER;

import com.epam.jym.crm.dto.TrainerWorkloadUpdateRequest;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadPublisher {

  private final JmsTemplate jmsTemplate;
  private final TrainerWorkloadMessagingProperties properties;

  private static Message attachTraceId(Message message, String traceId) throws JMSException {
    if (StringUtils.hasText(traceId)) {
      message.setStringProperty(TRACE_ID_HEADER, traceId);
    }
    return message;
  }

  public void publish(TrainerWorkloadUpdateRequest request, String traceId) {
    jmsTemplate.convertAndSend(
        properties.queue(), request, message -> attachTraceId(message, traceId));
  }
}
