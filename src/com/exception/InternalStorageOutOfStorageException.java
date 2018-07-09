package com.exception;

public class InternalStorageOutOfStorageException extends OSException{
    public InternalStorageOutOfStorageException(String message){
        super(message);
    }

    public InternalStorageOutOfStorageException(int exceptionCode, String message){
        super(exceptionCode, message);
    }

    public InternalStorageOutOfStorageException(int exceptionCode){
        super(exceptionCode);
    }

    public InternalStorageOutOfStorageException(ExceptionEnum exceptionEnum){
        super(exceptionEnum);
    }
}
