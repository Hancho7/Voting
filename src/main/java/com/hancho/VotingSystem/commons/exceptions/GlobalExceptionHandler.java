package com.hancho.VotingSystem.commons.exceptions;

import java.time.LocalDateTime;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
    logger.error("Illegal argument: {}", e.getMessage());

    Map<String, Object> error =
        Map.of(
            "error", "Invalid request",
            "message", e.getMessage(),
            "timestamp", LocalDateTime.now(),
            "status", HttpStatus.BAD_REQUEST.value());

    return ResponseEntity.badRequest().body(error);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<Map<String, Object>> handleRuntimeException(RuntimeException e) {
    logger.error("Runtime exception: {}", e.getMessage(), e);

    Map<String, Object> error =
        Map.of(
            "error",
            "Internal server error",
            "message",
            "An unexpected error occurred",
            "timestamp",
            LocalDateTime.now(),
            "status",
            HttpStatus.INTERNAL_SERVER_ERROR.value());

    return ResponseEntity.internalServerError().body(error);
  }
}
