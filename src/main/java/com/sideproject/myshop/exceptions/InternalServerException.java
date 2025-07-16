package com.sideproject.myshop.exceptions;

public class InternalServerException extends RuntimeException {
    public InternalServerException(String s){
        super(s);
    }
    public InternalServerException(String s, Throwable cause) {
        super(s, cause);
    }
}
