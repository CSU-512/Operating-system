package com.fileSystem;

public enum FileTypeEnum {
    INODE_IS_DIRECTORY("directory"),
    INODE_IS_REGULAR_FILE("regular file"),
    INODE_IS_SYMBOLIC_LINK("symbolic link");

    private String fileTypeDesc;

    FileTypeEnum(String fileTypeDesc){
        this.fileTypeDesc = fileTypeDesc;
    }

    public String getFileTypeDesc() {
        return fileTypeDesc;
    }
}
