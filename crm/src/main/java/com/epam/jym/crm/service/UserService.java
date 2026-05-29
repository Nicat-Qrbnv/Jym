package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;

public interface UserService {

  User register(UserCreateDto user);

  void changePassword(String username, String newPassword);

  void changeUserStatus(String username);

  void deactivateUser(Long userId);
}
