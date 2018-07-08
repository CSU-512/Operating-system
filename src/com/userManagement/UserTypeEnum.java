package com.userManagement;

public enum UserTypeEnum {
    OS_SUPERUSER("super user"),
    OS_USER("user"),
    OS_GUEST("guest");

    private String userType;

    UserTypeEnum(String userType){
        this.userType = userType;
    }

    public String getUserType() {
        return userType;
    }
}
