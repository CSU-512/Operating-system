package com.exception;

public class ExternalStorageOutOfStorageException extends OSException {

    public ExternalStorageOutOfStorageException(String message){
        super(message);
    }

    public ExternalStorageOutOfStorageException(int exceptionCode, String message){
        super(exceptionCode, message);
    }

    public ExternalStorageOutOfStorageException(int exceptionCode){
        super(exceptionCode);
    }

    public ExternalStorageOutOfStorageException(ExceptionEnum exceptionEnum){
        super(exceptionEnum);
    }
}
