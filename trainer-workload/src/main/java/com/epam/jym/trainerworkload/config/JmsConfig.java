package com.epam.jym.trainerworkload.config;

import jakarta.jms.ConnectionFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.jms.autoconfigure.DefaultJmsListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
@EnableJms
public class JmsConfig {

  @Bean
  public MessageConverter jacksonJmsMessageConverter() {
    JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
    converter.setTargetType(MessageType.TEXT);
    converter.setTypeIdPropertyName("_type");
    return converter;
  }

  @Bean
  public DefaultJmsListenerContainerFactory trainerWorkloadJmsListenerContainerFactory(
      DefaultJmsListenerContainerFactoryConfigurer configurer,
      @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
      MessageConverter messageConverter) {
    DefaultJmsListenerContainerFactory factory = new DefaultJmsListenerContainerFactory();
    configurer.configure(factory, connectionFactory);
    factory.setMessageConverter(messageConverter);
    factory.setSessionTransacted(true);
    return factory;
  }
}
