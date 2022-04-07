package userservice.controller;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;

/**
 * Class handling exceptions thrown by Service in Controller and generate the HTTP response.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ControllerExceptionHandler.class);

  /**
   * Handle UserNotFoundException thrown when the user can't be found.
   *

   * @param ex instance of the exception
   * @return HTTP 404 response
   */
  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response : 404 {}", error);
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
  }

  /**
   * Handle UserNameAlreadyUsedException thrown when the username is already used.
   *

   * @param ex instance of the exception
   * @return HTTP 409 response
   */
  @ExceptionHandler(UserNameAlreadyUsedException.class)
  public ResponseEntity<String> handleUserNameAlreadyUsedException(UserNameAlreadyUsedException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response : 409 {}", error);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Handle MethodArgumentNotValidException thrown when validation failed.
   *

   * @param ex instance of the exception
   * @return HTTP 422 response with information on invalid fields
   */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      MethodArgumentNotValidException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getAllErrors().forEach(error -> {
      String name = ((FieldError) error).getField();
      String errorMessage = error.getDefaultMessage();
      errors.put(name, errorMessage);
    });
    LOGGER.info("Response : 422 invalid DTO");
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
  }

}
