package com.exception;

public class ExternalStorageSizeException extends Exception {

    private int exceptionCode;

    public ExternalStorageSizeException(String message){
        super(message);
    }

    public ExternalStorageSizeException(int exceptionCode, String message){
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public ExternalStorageSizeException(int exceptionCode){
        super(ExceptionEnum.code2Message(exceptionCode));
        this.exceptionCode = exceptionCode;
    }

    public ExternalStorageSizeException(ExceptionEnum exceptionEnum){
        super(exceptionEnum.getExceptionMessage());
        this.exceptionCode = exceptionEnum.getExceptionCode();
    }

    public int getExceptionCode() {
        return exceptionCode;
    }

    public void printExceptionMessage(){
        System.out.println(this.exceptionCode + " " + this.getMessage());
    }
}
