package com.fileSystem;

import com.exception.ExceptionEnum;
import com.exception.OSException;
import com.userManagement.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class INode implements Serializable {
    private static final long serialVersionUID = 1L;
    private int iNumber;    //inode编号,OK
    private int parentINumber;   //父结点inode编号,OK
    private String fileName;    //文件名,OK
    private int fileLength;	// 文件字节数,OK
    private FileTypeEnum fileType;//文件类型,OK（除了链接文件）
    private int userID;	// 文件拥有者ID,NO
    private int groupID;	// 文件组ID,NO
    private int privilege;	// 文件权限,NO
    private Date ctime, mtime, atime; 	// inode上次变动时间、文件内容上次变动时间、文件上次打开时间,OK
    private int linkCount;	// 文件链接数,NO
    private ArrayList<Integer> dataBlockList; 	// 文件数据块列表，供文件使用,OK
    private Map<String, Integer> pathMap;   //从子inode编号到其路径名的映射，供目录使用,OK

    public INode() {

    }

    public INode(int iNumber, int parentINumber, String fileName, int fileLength, FileTypeEnum fileType, int userID, int groupID, int privilege, Date ctime, Date mtime, Date atime, int linkCount, ArrayList<Integer> dataBlockList, Map<String, Integer> pathMap) {
        this.iNumber = iNumber;
        this.parentINumber = parentINumber;
        this.fileName = fileName;
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
        this.pathMap = pathMap;
    }

    public int getiNumber() {
        return iNumber;
    }

    public void setiNumber(int iNumber) {
        this.iNumber = iNumber;
    }

    public int getParentINumber() {
        return parentINumber;
    }

    public void setParentINumber(int parentINumber) {
        this.parentINumber = parentINumber;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
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

    public int getUserID() {
        return userID;
    }

    public void setUserID(int userID) {
        this.userID = userID;
    }

    public int getGroupID() {
        return groupID;
    }

    public void setGroupID(int groupID) {
        this.groupID = groupID;
    }

    public int getPrivilege() {
        return privilege;
    }

    /**
     *
     * @param performer 执行者
     * @param privilege 欲设置的权限
     * @throws OSException  如果执行者最大权限小于预设值的权限，将抛出异常10022
     */
    public void setPrivilege(User performer, int privilege) throws OSException {
        if(performer.getUserType().getUserMaximumFilePrivilege() < privilege)
            throw new OSException(ExceptionEnum.OS_WEAK_ROLE_EXCEPTION);
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

    public Map<String, Integer> getPathMap() {
        return pathMap;
    }

    public void setPathMap(Map<String, Integer> pathMap) {
        this.pathMap = pathMap;
    }
}
