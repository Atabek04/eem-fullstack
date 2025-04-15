package kz.muhammadzahid.eem.exception;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.security.SignatureException;
import kz.muhammadzahid.eem.dto.ErrorResponse;
import kz.muhammadzahid.eem.util.RoleUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({
            UsernameAlreadyExistsException.class,
            EmailAlreadyExistsException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleConflictExceptions(Exception ex) {
        log.error("Conflict error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.CONFLICT, ex.getMessage(), null);
    }

    @ExceptionHandler({
            BadCredentialsException.class,
            AuthenticationException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleAuthenticationExceptions(Exception ex) {
        log.error("Authentication error: {}", ex.getMessage());
        String message = ex instanceof BadCredentialsException ? "Invalid username or password" : ex.getMessage();
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, null);
    }

    @ExceptionHandler({
            JwtException.class,
            SignatureException.class
    })
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleJwtExceptions(Exception ex) {
        log.error("JWT error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, "Invalid token", null);
    }

    @ExceptionHandler({
            UsernameNotFoundException.class,
            UserNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundException(Exception ex) {
        log.error("Not found: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.NOT_FOUND, ex.getMessage(), null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        log.error("Validation error: {}", errors);
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "Validation failed", errors);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(BadRequestException ex) {
        log.error("Bad request: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), null);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
    }

    @ExceptionHandler(org.springframework.web.HttpRequestMethodNotSupportedException.class)
    @ResponseStatus(HttpStatus.METHOD_NOT_ALLOWED)
    public ErrorResponse handleMethodNotSupportedException(org.springframework.web.HttpRequestMethodNotSupportedException ex) {
        var supportedMethods = String.join(", ", Objects.requireNonNull(ex.getSupportedMethods()));
        var message = String.format("Request method '%s' is not supported. Supported methods: %s",
                ex.getMethod(), supportedMethods);

        log.error("Method not allowed: {}", message);
        return buildErrorResponse(HttpStatus.METHOD_NOT_ALLOWED, message, null);
    }

    @ExceptionHandler(InvalidTokenException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ErrorResponse handleInvalidTokenException(InvalidTokenException ex) {
        log.error("Token error: {}", ex.getMessage());

        String message;
        if (ex.getMessage() != null && ex.getMessage().contains("expired")) {
            message = "Authentication failed: Token has expired";
        } else {
            message = "Authentication failed: Invalid token";
        }
        return buildErrorResponse(HttpStatus.UNAUTHORIZED, message, null);
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        var errorMessage = "Invalid JSON format in request body";

        if (ex.getCause() instanceof JsonParseException exception) {
            errorMessage = "JSON parse error: " + exception.getOriginalMessage();
        } else if (ex.getCause() instanceof MismatchedInputException) {
            errorMessage = "JSON parse error: Input doesn't match expected format";
        } else if (ex.getMessage() != null && !ex.getMessage().isEmpty() && ex.getMessage().contains(": ")) {
            errorMessage = "JSON parse error: " + ex.getMessage()
                    .substring(ex.getMessage().indexOf(": ") + 2);
        }

        log.error("JSON parsing error: {}", ex.getMessage());
        return buildErrorResponse(HttpStatus.BAD_REQUEST, errorMessage, null);
    }

    @ExceptionHandler(InvalidRoleException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleInvalidRoleException(InvalidRoleException ex) {
        log.error("Invalid role error: {}", ex.getMessage());
        String message = ex.getMessage() + " " + RoleUtil.getAvailableRolesMessage();
        return buildErrorResponse(HttpStatus.BAD_REQUEST, message, null);
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, List<String> errors) {
        return new ErrorResponse(
                status.value(),
                status.getReasonPhrase(),
                message,
                errors,
                LocalDateTime.now()
        );
    }
}