package com.epam.jym.crm.service;

import com.epam.jym.crm.dto.auth.CredentialsDto;

public interface AuthenticationService {

  void authenticate(CredentialsDto credentials);
}
