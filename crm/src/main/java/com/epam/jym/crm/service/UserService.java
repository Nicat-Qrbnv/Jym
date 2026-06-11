package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.user.PasswordUpdateDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;

public interface UserService {

  User register(UserCreateDto user);

  void changePassword(String username, PasswordUpdateDto passwordUpdateDto);

  void changeUserStatus(String username, boolean isActive);

  void deactivateUser(Long userId);
}
