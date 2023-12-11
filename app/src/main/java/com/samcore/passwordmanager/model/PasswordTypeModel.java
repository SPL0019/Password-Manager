package com.samcore.passwordmanager.model;

public class PasswordTypeModel {
    private String passwordType;
    private String UpdateUserID;

    public PasswordTypeModel() {
    }

    public String getPasswordType() {
        return passwordType;
    }

    public void setPasswordType(String passwordType) {
        this.passwordType = passwordType;
    }

    public String getUpdateUserID() {
        return UpdateUserID;
    }

    public void setUpdateUserID(String updateUserID) {
        UpdateUserID = updateUserID;
    }
}
