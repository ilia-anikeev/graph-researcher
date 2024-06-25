package com.graphResearcher.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.graphResearcher.dto.UserLoginDto;
import com.graphResearcher.dto.UserRegistrationDto;

import com.graphResearcher.exceptions.InvalidPassword;
import com.graphResearcher.exceptions.UserAlreadyExist;
import com.graphResearcher.exceptions.InvalidEmail;
import com.graphResearcher.exceptions.UserNotFoundException;
import com.graphResearcher.model.User;


import com.graphResearcher.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@CrossOrigin
@RestController
public class AuthorizationController {
    private static final Logger log = LoggerFactory.getLogger(GraphResearchController.class);

    private final UserService userService;

    @Autowired
    public AuthorizationController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(HttpServletRequest request) {
        try {
            String jsonString = request.getReader().lines().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(jsonString);
            UserRegistrationDto registrationDto = new UserRegistrationDto(json);
            User user = userService.registerUser(registrationDto);
            return ResponseEntity.ok(Integer.toString(user.getUserID()));
        } catch (UserAlreadyExist | InvalidEmail e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(HttpServletRequest request) {
        try {
            String jsonString = request.getReader().lines().collect(Collectors.joining());
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(jsonString);
            UserLoginDto loginDto = new UserLoginDto(json);
            User user = userService.loginUser(loginDto);
            return ResponseEntity.ok(Integer.toString(user.getUserID()));
        } catch (UserNotFoundException | InvalidPassword e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Throwable e) {
            log.error("Server error", e);
            return ResponseEntity.internalServerError().build();
        }
    }
}