package com.fileSystem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class INode implements Serializable {
    private int iNumber;    //inode编号
    private int fileLength;	// 文件字节数
    private FileTypeEnum fileType;//文件类型
    private String userID;	// 文件拥有者ID
    private String groupID;	// 文件组ID
    private int privilege;	// 文件权限
    private Date ctime, mtime, atime; 	// inode上次变动时间、文件内容上次变动时间、文件上次打开时间
    private int linkCount;	// 文件链接数
    private ArrayList<Integer> dataBlockList; 	// 文件数据块列表
    private ArrayList<Integer> childInodeList;  //  保存所有子inode的编号
    private Map<String, Integer> pathMap;   //从子inode编号到其路径名的映射

    public INode() {

    }

    public INode(int iNumber, int fileLength, FileTypeEnum fileType, String userID, String groupID, int privilege, Date ctime, Date mtime, Date atime, int linkCount, ArrayList<Integer> dataBlockList, ArrayList<Integer> childInodeList, Map<String, Integer> pathMap) {
        this.iNumber = iNumber;
        this.fileLength = fileLength;
        this.fileType = fileType;
        this.userID = userID;
        this.groupID = groupID;
        this.privilege = privilege;
        this.ctime = ctime;
        this.mtime = mtime;
        this.atime = atime;
        this.linkCount = linkCount;
        this.dataBlockList = dataBlockList;
        this.childInodeList = childInodeList;
        this.pathMap = pathMap;
    }

    public int getiNumber() {
        return iNumber;
    }

    public void setiNumber(int iNumber) {
        this.iNumber = iNumber;
    }

    public int getFileLength() {
        return fileLength;
    }

    public void setFileLength(int fileLength) {
        this.fileLength = fileLength;
    }

    public FileTypeEnum getFileType() {
        return fileType;
    }

    public void setFileType(FileTypeEnum fileType) {
        this.fileType = fileType;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getGroupID() {
        return groupID;
    }

    public void setGroupID(String groupID) {
        this.groupID = groupID;
    }

    public int getPrivilege() {
        return privilege;
    }

    public void setPrivilege(int privilege) {
        this.privilege = privilege;
    }

    public Date getCtime() {
        return ctime;
    }

    public void setCtime(Date ctime) {
        this.ctime = ctime;
    }

    public Date getMtime() {
        return mtime;
    }

    public void setMtime(Date mtime) {
        this.mtime = mtime;
    }

    public Date getAtime() {
        return atime;
    }

    public void setAtime(Date atime) {
        this.atime = atime;
    }

    public int getLinkCount() {
        return linkCount;
    }

    public void setLinkCount(int linkCount) {
        this.linkCount = linkCount;
    }

    public ArrayList<Integer> getDataBlockList() {
        return dataBlockList;
    }

    public void setDataBlockList(ArrayList<Integer> dataBlockList) {
        this.dataBlockList = dataBlockList;
    }

    public ArrayList<Integer> getChildInodeList() {
        return childInodeList;
    }

    public void setChildInodeList(ArrayList<Integer> childInodeList) {
        this.childInodeList = childInodeList;
    }

    public Map<String, Integer> getPathMap() {
        return pathMap;
    }

    public void setPathMap(Map<String, Integer> pathMap) {
        this.pathMap = pathMap;
    }
}
