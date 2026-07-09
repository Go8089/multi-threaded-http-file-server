package com.goMaddy.multithreaded_http_fileserver.exception;

import com.goMaddy.multithreaded_http_fileserver.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class GlobalExceptionHandler {
        private static final Logger logger =LoggerFactory.getLogger(GlobalExceptionHandler.class);
        private ResponseEntity<ErrorResponse> buildErrorResponse(
        HttpStatus status,
        String message,
        HttpServletRequest request) {

        ErrorResponse error = new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI()
        );
       return ResponseEntity.status(status).body(error);
       }
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<ErrorResponse> handleFileStorageException(
            FileStorageException ex,
            HttpServletRequest request) {
            logger.error("File storage error at {} : {}", request.getRequestURI(),  ex.getMessage(),  ex);
        return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            request
    );
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(
            ResourceNotFoundException ex,
            HttpServletRequest request) {
            logger.error("Resource not found at {} : {}", request.getRequestURI(),  ex.getMessage());
           return buildErrorResponse(
            HttpStatus.NOT_FOUND,
            ex.getMessage(),
            request
    );
    }
    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExistsException(
        UserAlreadyExistsException ex,
        HttpServletRequest request) {
        logger.warn("Registration conflict: {}", ex.getMessage());
        return buildErrorResponse(
            HttpStatus.CONFLICT,
            ex.getMessage(),
            request
        );
      }
      @ExceptionHandler(BadCredentialsException.class)
        public ResponseEntity<ErrorResponse> handleBadCredentials(
        BadCredentialsException ex,
        HttpServletRequest request) {
       logger.warn("Authentication failed: {}", ex.getMessage());
    return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage(),
            request
    );
}
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(
            InvalidCredentialsException ex,
            HttpServletRequest request) {
       logger.warn("Invalid credentials provided: {}", ex.getMessage());
       return buildErrorResponse(
            HttpStatus.UNAUTHORIZED,
            ex.getMessage(),
            request);
    }
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex, HttpServletRequest request) {
        logger.warn("Access denied at {} : {}",request.getRequestURI(),ex.getMessage());       
        return buildErrorResponse(
            HttpStatus.FORBIDDEN,
            ex.getMessage(),
            request);}

        @ExceptionHandler(Exception.class)
public ResponseEntity<ErrorResponse> handleException(
        Exception ex,
        HttpServletRequest request) {

    logger.error(
            "Unexpected exception at {}",
            request.getRequestURI(),
            ex
    );

    ErrorResponse error = new ErrorResponse(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "Something went wrong.",
            request.getRequestURI()
    );

    return ResponseEntity
            .status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(error);
}
       @ExceptionHandler(IllegalArgumentException.class)
public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
        IllegalArgumentException ex,
        HttpServletRequest request) {
       logger.warn(
            "Illegal argument at {} : {}",
            request.getRequestURI(),
            ex.getMessage()
    );
    return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            ex.getMessage(),
            request);}
            @ExceptionHandler(MethodArgumentNotValidException.class)
public ResponseEntity<ErrorResponse> handleValidationException(
        MethodArgumentNotValidException ex,
        HttpServletRequest request) {

    String message = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(error ->
                    error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("Validation failed");

    logger.warn(
            "Validation failed at {} : {}",
            request.getRequestURI(),
            message
    );

    return buildErrorResponse(
            HttpStatus.BAD_REQUEST,
            message,
            request);}
}
