package com.stanislav.merci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UserPointsWasDeletedException extends RuntimeException{
    public UserPointsWasDeletedException(String msg) {
        super(msg);
    }
}
