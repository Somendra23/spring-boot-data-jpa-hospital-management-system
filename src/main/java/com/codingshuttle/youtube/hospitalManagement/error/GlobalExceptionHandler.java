package com.codingshuttle.youtube.hospitalManagement.error;

import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNameNotFound(UsernameNotFoundException exception){
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .errorMessage("User not found")
                .httpStatus(HttpStatus.NOT_FOUND)
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(apiError);
    }

    @ExceptionHandler({JwtException.class,AccessDeniedException.class})
    public ResponseEntity<ApiError> handleJwtException(JwtException exception){
        ApiError apiError = ApiError.builder()
                .timestamp(LocalDateTime.now())
                .errorMessage("Invalid JWT token")
                .httpStatus(HttpStatus.UNAUTHORIZED)
                .build();
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(apiError);
    }

   @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAllOtherExceptions(Exception exception){
        ApiError apiError = ApiError.builder().timestamp(LocalDateTime.now())
                .errorMessage("Internal Server Error"+exception.getMessage()).httpStatus(HttpStatus.INTERNAL_SERVER_ERROR).build();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(apiError);

    }
}
