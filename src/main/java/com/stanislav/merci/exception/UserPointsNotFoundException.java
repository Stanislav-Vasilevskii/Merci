package com.stanislav.merci.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class UserPointsNotFoundException extends RuntimeException{
    public UserPointsNotFoundException(String msg) {
        super(msg);
    }
}
