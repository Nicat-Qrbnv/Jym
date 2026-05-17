package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.RegisteredUserDto;
import com.epam.jym.crm.dto.UserCreateDto;

public interface AuthenticationService {

  RegisteredUserDto register(UserCreateDto user);
}
