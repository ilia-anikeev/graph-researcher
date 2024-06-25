package com.graphResearcher.dto;

import com.fasterxml.jackson.databind.JsonNode;

public class UserRegistrationDto {
    private final String username;
    private final String email;
    private final String password;

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }


    public String getPassword() {
        return password;
    }

    public UserRegistrationDto(JsonNode json) {
        username = json.get("username").asText();
        email = json.get("email").asText();
        password = json.get("password").asText();
    }

    public UserRegistrationDto(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
}