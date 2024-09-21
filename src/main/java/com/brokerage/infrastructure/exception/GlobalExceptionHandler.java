package com.brokerage.infrastructure.exception;

import com.brokerage.presentation.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.Objects;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

  /**
   * Handle IllegalArgumentException (business validation errors)
   */
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Handle authentication-related exceptions
   */
  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ApiResponse<Void>> handleAuthenticationException(AuthenticationException ex) {
    return buildResponse(HttpStatus.UNAUTHORIZED, "Authentication failed: " + ex.getMessage());
  }

  /**
   * Handle validation errors (JSR-303 bean validation)
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException ex) {
    String errorMessage = ex.getBindingResult().getFieldErrors().stream()
                            .map(error -> error.getField() + ": " + error.getDefaultMessage())
                            .findFirst()
                            .orElse("Invalid request data");
    return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  /**
   * Handle ConstraintViolationException (direct validation failures)
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ApiResponse<Void>> handleConstraintViolationException(ConstraintViolationException ex) {
    return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  /**
   * Handle missing request parameters
   */
  @ExceptionHandler(MissingServletRequestParameterException.class)
  public ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
    String errorMessage = "Missing required parameter: " + ex.getParameterName();
    return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  /**
   * Handle MethodArgumentTypeMismatchException (invalid parameter types)
   */
  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
    String errorMessage = "Invalid value for parameter '" + ex.getName() + "'. Expected type: " + Objects.requireNonNull(
        ex.getRequiredType()).getSimpleName();
    return buildResponse(HttpStatus.BAD_REQUEST, errorMessage);
  }

  /**
   * Handle all other exceptions
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ApiResponse<Void>> handleGeneralException(Exception ex) {
    return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred: " + ex.getMessage());
  }

  /**
   * Utility method to build an ApiResponse
   */
  private ResponseEntity<ApiResponse<Void>> buildResponse(HttpStatus status, String message) {
    ApiResponse<Void> apiResponse = ApiResponse.failure(message);
    return new ResponseEntity<>(apiResponse, status);
  }
}