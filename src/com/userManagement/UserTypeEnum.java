package com.userManagement;

public enum UserTypeEnum {
    OS_SUPERUSER(511, "super user"),
    OS_USER(63, "user"),
    OS_GUEST(7, "guest");

    private String userType;
    private int filePrivilege;

    UserTypeEnum(int filePrivilege, String userType) {
        this.userType = userType;
        this.filePrivilege = filePrivilege;
    }

    public String getUserType() {
        return userType;
    }

    public int getUserMaximumFilePrivilege() {
        return filePrivilege;
    }

    public static UserTypeEnum getUserTypeByString(String type){
        UserTypeEnum userTypeEnum = null;
            for(UserTypeEnum ute : UserTypeEnum.values())
                if(ute.getUserType().equals(type))
                    userTypeEnum = ute;
        return userTypeEnum;
    }
}
