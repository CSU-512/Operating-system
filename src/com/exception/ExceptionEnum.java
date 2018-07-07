package com.exception;

public enum ExceptionEnum {
    OS_EXTERNAL_STORAGE_SIZE_EXCEPTION(10001, "外存容量应该为盘块大小的倍数"),
    OS_EXTERNAL_STORAGE_OUT_OF_STORAGE_EXCEPTION(10002, "外存容量不足"),
    OS_OTHER_EXCEPTION(99999, "其他错误");

    private int exceptionCode;
    private String exceptionMessage;

    private ExceptionEnum(int exceptionCode, String exceptionMessage){
        this.exceptionCode = exceptionCode;
        this.exceptionMessage = exceptionMessage;
    }

    // 异常信息和异常代码的getter
    public String getExceptionMessage(){
        return this.exceptionMessage;
    }
    public int getExceptionCode(){
        return this.exceptionCode;
    }

    // 根据异常代码得到异常信息
    public static String code2Message(int exceptionCode){
        for(ExceptionEnum exceptionEnum : ExceptionEnum.values()){
            if(exceptionCode == exceptionEnum.getExceptionCode()){
                return exceptionEnum.getExceptionMessage();
            }
        }
        return OS_OTHER_EXCEPTION.getExceptionMessage();
    }
}