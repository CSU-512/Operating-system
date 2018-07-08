package com.exception;

public class NoSuchUserException extends OSException {
    public NoSuchUserException(String message){
        super(message);
    }

    public NoSuchUserException(int exceptionCode, String message){
        super(exceptionCode, message);
    }

    public NoSuchUserException(int exceptionCode){
        super(exceptionCode);
    }

    public NoSuchUserException(ExceptionEnum exceptionEnum){
        super(exceptionEnum);
    }

}
