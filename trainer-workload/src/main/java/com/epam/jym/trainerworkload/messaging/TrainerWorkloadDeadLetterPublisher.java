package com.epam.jym.trainerworkload.messaging;

import static com.epam.jym.trainerworkload.logging.TraceLoggingConstants.TRACE_ID_HEADER;

import jakarta.jms.JMSException;
import jakarta.jms.Message;
import lombok.RequiredArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class TrainerWorkloadDeadLetterPublisher {

  private final JmsTemplate jmsTemplate;
  private final TrainerWorkloadMessagingProperties properties;

  public void publish(TrainerWorkloadDeadLetterMessage message, String traceId) {
    jmsTemplate.convertAndSend(
        properties.dlq(), message, jmsMessage -> attachTraceId(jmsMessage, traceId));
  }

  private static Message attachTraceId(Message message, String traceId) throws JMSException {
    if (StringUtils.hasText(traceId)) {
      message.setStringProperty(TRACE_ID_HEADER, traceId);
    }
    return message;
  }
}
