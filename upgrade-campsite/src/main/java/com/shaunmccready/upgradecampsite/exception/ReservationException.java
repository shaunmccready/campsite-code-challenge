package com.shaunmccready.upgradecampsite.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Exception thats thrown when a client tries to reserve a day thats already taken
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class ReservationException extends RuntimeException {

    public ReservationException(String message) {
        super(message);
    }

}
