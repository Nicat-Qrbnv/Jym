package com.epam.jym.crm.dto;

public record RegisteredUserDto(
    Long id, String firstName, String lastName, String username, String password) {}
