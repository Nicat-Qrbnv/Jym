package com.epam.jym.jwthandler.config;

import com.epam.jym.jwthandler.service.JwtService;
import com.epam.jym.jwthandler.service.impl.JwtServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(JwtProperties.class)
public class JwtHandlerAutoConfiguration {

  @Bean
  @ConditionalOnMissingBean
  public JwtService jwtService(JwtProperties properties) {
    return new JwtServiceImpl(properties);
  }
}
