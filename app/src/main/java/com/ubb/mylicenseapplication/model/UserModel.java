package com.ubb.mylicenseapplication.model;

import java.io.Serializable;

public class UserModel implements Serializable {

    private String userName;
    private String name;
    private int userRole;

    public UserModel(String userName, String name, int userRole) {
        this.userName = userName;
        this.name = name;
        this.userRole = userRole;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getUserRole() {
        return userRole;
    }

    public void setUserRole(int userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return "UserModel{" +
                "userName='" + userName + '\'' +
                ", name='" + name + '\'' +
                ", userRole=" + userRole +
                '}';
    }
}
