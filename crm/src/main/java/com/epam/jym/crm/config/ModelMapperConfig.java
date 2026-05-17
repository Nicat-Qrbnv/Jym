package com.epam.jym.crm.config;

import com.epam.jym.crm.dto.RegisteredUserDto;
import com.epam.jym.crm.entity.User;
import org.hibernate.type.MappingContext;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ModelMapperConfig {

  @Bean
  ModelMapper modelMapper() {
    ModelMapper mapper = new ModelMapper();
    mapper.addConverter(getRegisteredUserDto());
    return mapper;
  }

  private Converter<User, RegisteredUserDto> getRegisteredUserDto() {
    return ctx -> {
      User source = ctx.getSource();
      if (source == null) {
        return null;
      }
      return new RegisteredUserDto(
          source.getId(),
          source.getFirstName(),
          source.getLastName(),
          source.getUsername(),
          source.getPassword());
    };
  }
}
