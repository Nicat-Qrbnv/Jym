package com.epam.jym.crm.service;

import com.epam.jym.crm.entity.User;

public interface AuthenticationService {

  <U extends User> U register(U user);

  String generateUsername(String firstName, String lastName);
}
