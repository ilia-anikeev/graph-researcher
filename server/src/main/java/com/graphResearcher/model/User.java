package com.graphResearcher.model;

import lombok.Getter;

@Getter
public class User {

    private Integer userID;

    private final String username;

    private final String email;

    private final String password;

    public User (String email, String username, String password){
        this.email = email;
        this.username = username;
        this.password = password;
    }

    public User (String email, String username, String password, int userID){
        this.email = email;
        this.username = username;
        this.password = password;
        this.userID = userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }
}