package com.exception;

public class ExternalStorageSizeException extends OSException {
    public ExternalStorageSizeException(String message){
        super(message);
    }

    public ExternalStorageSizeException(int exceptionCode, String message){
        super(exceptionCode, message);
    }

    public ExternalStorageSizeException(int exceptionCode){
        super(exceptionCode);
    }

    public ExternalStorageSizeException(ExceptionEnum exceptionEnum){
        super(exceptionEnum);
    }
}
