package com.stanislav.merci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserPointsAlreadyExistsException extends RuntimeException{
    public UserPointsAlreadyExistsException(String msg) {
        super(msg);
    }
}
