package com.fileSystem;

import java.util.Date;

public class INode {
    private int fileLength;	// 文件字节数
    private FileTypeEnum fileType;//文件类型
    private String userID;	// 文件拥有者ID
    private String GroupID;	// 文件组ID
    private int privilege;	// 文件权限
    private Date ctime, mtime, atime; 	// inode上次变动时间、文件内容上次变动时间、文件上次打开时间
    private int linkCount;	// 文件链接数
    private int dataBlockPosition; 		// 文件数据块的位置

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
        return GroupID;
    }

    public void setGroupID(String groupID) {
        GroupID = groupID;
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

    public int getDataBlockPosition() {
        return dataBlockPosition;
    }

    public void setDataBlockPosition(int dataBlockPosition) {
        this.dataBlockPosition = dataBlockPosition;
    }
}
