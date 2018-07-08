package com.userManagement;

public enum UserTypeEnum {
    OS_SUPERUSER(511, "super user"),
    OS_USER(255, "user"),
    OS_GUEST(7, "guest");

    private String userType;
    private int privilege;

    UserTypeEnum(int privilege,String userType){
        this.userType = userType;
        this.privilege = privilege;
    }

    public String getUserType() {
        return userType;
    }
}
