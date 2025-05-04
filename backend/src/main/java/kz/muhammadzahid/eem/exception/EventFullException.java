package kz.muhammadzahid.eem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class EventFullException extends RuntimeException {
    public EventFullException(String message) {
        super(message);
    }
}