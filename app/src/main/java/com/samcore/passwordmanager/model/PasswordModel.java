package com.samcore.passwordmanager.model;

public class PasswordModel {
    private String id;
    private String name;
    private String username;
    private String password;
    private String password_type;

    public PasswordModel() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword_type() {
        return password_type;
    }

    public void setPassword_type(String password_type) {
        this.password_type = password_type;
    }
}
