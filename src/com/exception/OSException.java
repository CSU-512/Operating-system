package com.exception;

public class OSException extends Exception {
    private int exceptionCode;

    public OSException(String message){
        super(message);
    }

    public OSException(int exceptionCode, String message){
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public OSException(int exceptionCode){
        super(ExceptionEnum.code2Message(exceptionCode));
        this.exceptionCode = exceptionCode;
    }

    public OSException(ExceptionEnum exceptionEnum){
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
