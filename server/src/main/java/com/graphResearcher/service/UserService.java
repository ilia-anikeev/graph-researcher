package com.graphResearcher.service;

import com.graphResearcher.dto.UserLoginDto;
import com.graphResearcher.dto.UserRegistrationDto;
import com.graphResearcher.dto.UserUpdateDto;

import com.graphResearcher.model.User;

import com.graphResearcher.repository.UserManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private final UserManager userManager;

    @Autowired
    public UserService(UserManager userManager) {
        this.userManager = userManager;
    }

    public User registerUser(UserRegistrationDto registrationDto) {
        User existingUser = userManager.findByUsername(registrationDto.getUsername());
        if (existingUser != null) {
            throw new RuntimeException("Username already in use");
        }

        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(hashPassword(registrationDto.getPassword()));

        return userManager.createUser(user);
    }

    public User loginUser(UserLoginDto loginDto) {
        User user = userManager.findByUsername(loginDto.getUsername());
        if (user != null && user.getPassword().equals(hashPassword(loginDto.getPassword()))) {
            return user;
        }
        throw new RuntimeException("Invalid username or password");
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            BigInteger number = new BigInteger(1, hash);
            StringBuilder hexString = new StringBuilder(number.toString(16));
            while (hexString.length() < 32) {
                hexString.insert(0, '0');
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

//
//    public User getUser(String username) {
//        return userManager.findByUsername(username).orElse(null);
//    }

}