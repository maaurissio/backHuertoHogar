package com.huertohogar.huertohogar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class ForbiddenException extends RuntimeException {
    
    private final String errorCode;
    
    public ForbiddenException(String message) {
        super(message);
        this.errorCode = "SIN_PERMISOS";
    }
    
    public ForbiddenException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
}
