package tripservice.controller;

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

/**
 * Class handling exceptions thrown by Service in Controller and generate the HTTP response.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory
      .getLogger(ControllerExceptionHandler.class);

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
      String name;
      if (error instanceof FieldError) {
        name = ((FieldError) error).getField();
      } else {
        name = error.getObjectName();
      }
      String errorMessage = error.getDefaultMessage();
      errors.put(name, errorMessage);
    });
    LOGGER.info("Response : 422 invalid DTO");
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
  }

}
