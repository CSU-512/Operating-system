package com.exception;

public class ExternalStorageOutOfStorageException extends Exception {
    private int exceptionCode;

    public ExternalStorageOutOfStorageException(String message){
        super(message);
    }

    public ExternalStorageOutOfStorageException(int exceptionCode, String message){
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public ExternalStorageOutOfStorageException(int exceptionCode){
        super(ExceptionEnum.code2Message(exceptionCode));
        this.exceptionCode = exceptionCode;
    }

    public ExternalStorageOutOfStorageException(ExceptionEnum exceptionEnum){
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
