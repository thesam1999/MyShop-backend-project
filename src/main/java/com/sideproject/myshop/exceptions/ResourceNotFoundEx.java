package com.sideproject.myshop.exceptions;

//Spring提供註解，指這個例外被丟出時，Spring Boot 自動回傳HTTP狀態碼404
public class ResourceNotFoundEx extends RuntimeException {
    public ResourceNotFoundEx(String s) {
        super(s);
    }

    public ResourceNotFoundEx(String s, Throwable cause) {
        super(s, cause);
    }
}
