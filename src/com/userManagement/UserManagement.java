package com.userManagement;

import com.exception.ExceptionEnum;
import com.exception.NoSuchUserException;
import com.exception.OSException;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class UserManagement implements Serializable {
    private static final long serialVersionUID = 1L;
    private ArrayList<User> userList;
    private boolean[] UIDList = new boolean[1024]; // true表示UID已用；false表示UID可用

    // 用于构造从外存读入的UserManagement
    public UserManagement(ArrayList<User> userList, boolean[] UIDList) {
        this.userList = userList;
        this.UIDList = UIDList;
    }

    public UserManagement(File userManagementFile) throws IOException, ClassNotFoundException {

        FileInputStream fis = new FileInputStream(userManagementFile);
        byte[] userJSONByte = new byte[(int) new File("UserManagement.json").length()];
        fis.read(userJSONByte);

        JSONObject jo = new JSONObject(new String(userJSONByte));
        byte[] userByte = Base64.decodeBase64(jo.getString("UserManagement"));

        ByteArrayInputStream bis = new ByteArrayInputStream(userByte);
        ObjectInputStream oi = new ObjectInputStream(bis);
        UserManagement um = (UserManagement) oi.readObject();
        this.userList = um.userList;
        this.UIDList = um.UIDList;

        fis.close();
        bis.close();
        oi.close();

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
            User performer, String userName, String password, int GID, UserTypeEnum userType
    ) throws OSException {
        if(performer.getUserType().getUserMaximumFilePrivilege() < userType.getUserMaximumFilePrivilege())
            throw new OSException(ExceptionEnum.OS_WEAK_ROLE_EXCEPTION);
        int UID = UIDalloc();
        try {
            User newUser = new User(userType, userName, UID, GID, password);
            userList.add(newUser);
        } catch (NoSuchAlgorithmException e){
            e.printStackTrace();
        }
    }

    // 按用户名删除用户并释放UID
    public void deleteUser(User performer, String userName) throws OSException {
        User user = findUser(userName);
        if(user == null)
            throw new OSException(ExceptionEnum.OS_NO_SUCH_USER_EXCEPTION);
        if(performer.getUserType().getUserMaximumFilePrivilege() <= user.getUserType().getUserMaximumFilePrivilege())
            throw new OSException(ExceptionEnum.OS_WEAK_ROLE_EXCEPTION);
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

    public ArrayList<User> getUserList() {
        return userList;
    }
}
