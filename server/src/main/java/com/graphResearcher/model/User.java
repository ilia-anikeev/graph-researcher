package com.graphResearcher.model;

import java.util.Date;


public class User {

    private Integer userId;

    private String username;

    private String email;

    private String password;

    public User (String email, String username, String password){
        this.email=email;
        this.username=username;
        this.password=password;
    }
    public User(){}

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}