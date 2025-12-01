package com.huertohogar.huertohogar.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resource, Long id) {
        super(String.format("%s no encontrado con id: %d", resource, id));
    }
    
    public ResourceNotFoundException(String resource, String id) {
        super(String.format("%s no encontrado con id: %s", resource, id));
    }
}
