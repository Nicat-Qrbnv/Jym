package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.dto.user.UserProfileDto;
import com.epam.jym.crm.entity.User;
import com.epam.jym.crm.util.mapper.core.Mapper;
import com.epam.jym.crm.util.mapper.core.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UserMapping {

  @Bean
  Mapper<User, CredentialsDto> toCredentialsDto() {
    return new Mapper<>() {
      @Override
      public Class<User> sourceType() {
        return User.class;
      }

      @Override
      public Class<CredentialsDto> targetType() {
        return CredentialsDto.class;
      }

      @Override
      public CredentialsDto map(User source, MappingContext context) {
        return new CredentialsDto(source.getUsername(), source.getGeneratedPassword());
      }
    };
  }

  @Bean
  Mapper<User, UserDto> toUserDto() {
    return new Mapper<>() {
      @Override
      public Class<User> sourceType() {
        return User.class;
      }

      @Override
      public Class<UserDto> targetType() {
        return UserDto.class;
      }

      @Override
      public UserDto map(User source, MappingContext context) {
        return new UserDto(source.getFirstName(), source.getLastName(), source.isActive());
      }
    };
  }

  @Bean
  Mapper<User, UserProfileDto> toUserProfileDto() {
    return new Mapper<>() {
      @Override
      public Class<User> sourceType() {
        return User.class;
      }

      @Override
      public Class<UserProfileDto> targetType() {
        return UserProfileDto.class;
      }

      @Override
      public UserProfileDto map(User source, MappingContext context) {
        return new UserProfileDto(
            source.getUsername(), source.getFirstName(), source.getLastName());
      }
    };
  }
}
