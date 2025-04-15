package kz.muhammadzahid.eem.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRoleException extends RuntimeException {

    public InvalidRoleException(String roleName) {
        super("Invalid role name: " + roleName);
    }

    public InvalidRoleException(String roleName, String message) {
        super("Invalid role name: " + roleName + ". " + message);
    }

    public InvalidRoleException(String roleName, Throwable cause) {
        super("Invalid role name: " + roleName, cause);
    }
}
