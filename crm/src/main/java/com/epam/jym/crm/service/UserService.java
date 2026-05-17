package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.user.RegisteredUserDto;
import com.epam.jym.crm.dto.user.UserCreateDto;
import com.epam.jym.crm.entity.User;

public interface UserService {

  RegisteredUserDto register(UserCreateDto user);

  void changePassword(Long userId, String newPassword);


  void changeUserStatus(Long userId);

  User getUser(Long userId);
}
