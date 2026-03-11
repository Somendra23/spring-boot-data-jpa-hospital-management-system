package com.codingshuttle.youtube.hospitalManagement.error;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Builder
@Data
public class ApiError {
    private LocalDateTime timestamp;
    private String errorMessage;
    private HttpStatus httpStatus;
}
