package com.epam.jym.crm.util.mapper;

import com.epam.jym.crm.dto.user.CredentialsDto;
import com.epam.jym.crm.dto.user.UserDto;
import com.epam.jym.crm.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
  public CredentialsDto toCredentialsDto(User source) {
    if (source == null) {
      return null;
    }
    return new CredentialsDto(source.getUsername(), source.getPassword());
  }

  public UserDto toUserDto(User user) {
    return new UserDto(user.getFirstName(), user.getLastName(), user.isActive());
  }
}
