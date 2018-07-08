package com.userManagement;

import com.exception.ExceptionEnum;
import com.exception.NoSuchUserException;
import com.exception.OSException;

import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class UserManagement implements Serializable {
    private ArrayList<User> userList;
    private boolean[] UIDList = new boolean[1024]; // true表示UID已用；false表示UID可用

    // 用于构造从外存读入的UserManagement
    public UserManagement(ArrayList<User> userList, boolean[] UIDList) {
        this.userList = userList;
        this.UIDList = UIDList;
    }

    // 用于第一次生成UserManagement
    public UserManagement() {
        userList = new ArrayList<>();
        for(boolean i : UIDList)
            i = false;
    }

    // 按用户名查找用户并比对密码
    public boolean userLogin(String userName, String password) throws OSException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        String digestedPassword = User.bytesToHex(md.digest());
        User targetUser = findUser(userName);
        if(targetUser == null)
            throw new OSException(ExceptionEnum.OS_NO_SUCH_USER_EXCEPTION);
        return targetUser.getPassword().equals(digestedPassword);
    }
    // 按UID查找用户并比对密码
    public boolean userLogin(int UID, String password) throws OSException, NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(password.getBytes());
        String digestedPassword = User.bytesToHex(md.digest());
        User targetUser = findUser(UID);
        if(targetUser == null)
            throw new OSException(ExceptionEnum.OS_NO_SUCH_USER_EXCEPTION);
        return targetUser.getPassword().equals(digestedPassword);
    }

    // 指定用户名、密码、GID以及用户类型，建立新用户
    public void createNewUser(
            String userName, String password, int GID, UserTypeEnum userType
    ) {
        int UID = UIDalloc();
        try {
            User newUser = new User(userType, userName, UID, GID, password);
            userList.add(newUser);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    // 按用户名删除用户并释放UID
    public void deleteUser(String userName) throws OSException {
        User user = findUser(userName);
        if(user == null)
            throw new OSException(ExceptionEnum.OS_NO_SUCH_USER_EXCEPTION);
        userList.remove(user);
        UIDList[user.getUID()] = false;
    }
    // 按UID删除用户并释放UID
    public void deleteUser(int UID) throws OSException {
        User user = findUser(UID);
        if(user == null)
            throw new OSException(ExceptionEnum.OS_NO_SUCH_USER_EXCEPTION);
        userList.remove(user);
        UIDList[UID] = false;
    }

    // 线性扫描发分配UID
    private int UIDalloc(){
        int UID = 0;
        for(; UID < UIDList.length; UID++){
            if(!UIDList[UID]){
                UIDList[UID] = true;
                break;
            }
        }
        return UID;
    }

    // O(n)遍历与用户列表找到目标用户
    public User findUser(String userName){
        for(User u : userList)
            if(u.getUserName().equals(userName))
                return u;
        return null;
    }
    public User findUser(int UID){
        for(User u : userList)
            if(u.getUID() == UID)
                return u;
        return null;
    }
}
