package com.userManagement;

import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class User implements Serializable {
    private UserTypeEnum userType;  // 用户类型
    private String userName;        // 用户名
    private int UID;                // 用户ID
    private int GID;                // 用户组ID
    private String password;        // 用户密码，经过md5加密

    public User(UserTypeEnum userType, String userName, int UID, int GID, String password)
            throws NoSuchAlgorithmException {
        this.userType = userType;
        this.userName = userName;
        this.UID = UID;
        this.GID = GID;
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        this.password = bytesToHex(md.digest());

    }

    public UserTypeEnum getUserType() {
        return userType;
    }

    public String getUserName() {
        return userName;
    }

    public int getUID() {
        return UID;
    }

    public int getGID() {
        return GID;
    }

    public String getPassword() {
        return password;
    }

    // 将摘要内容转成十六进制字符串保存在域中
    public static String bytesToHex(byte[] digestion) {
        BigInteger bigInteger = new BigInteger(1, digestion);
        return bigInteger.toString(16);
    }
}
