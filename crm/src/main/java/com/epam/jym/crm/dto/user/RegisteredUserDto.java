package com.epam.jym.crm.dto.user;

public record RegisteredUserDto(
    Long id, String firstName, String lastName, String username, String password) {}
