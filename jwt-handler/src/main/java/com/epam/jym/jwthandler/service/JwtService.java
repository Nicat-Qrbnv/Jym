package com.epam.jym.jwthandler.service;

public interface JwtService {

  String generateToken(String username);

  String extractUsername(String token);

  boolean isValid(String token, String username);

  boolean isValid(String token);

  void revokeToken(String token);

  long expirationSeconds();
}
