package locationservice.controller;

import java.util.HashMap;
import java.util.Map;
import javax.validation.ConstraintViolationException;
import org.hibernate.validator.internal.engine.path.PathImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import shared.exception.NoLocationFoundException;

/**
 * Class handling exceptions thrown by Service in Controller and generate the HTTP response.
 */
@ControllerAdvice
public class ControllerExceptionHandler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerExceptionHandler.class);

  /**
   * Handle ForbiddenOperationException thrown when requesting an operation forbidden by business
   * rules.
   *
   * @param ex instance of the exception
   * @return HTTP 409 response
   */
  @ExceptionHandler(NoLocationFoundException.class)
  public ResponseEntity<String> handleNoLocationFoundException(NoLocationFoundException ex) {
    String error = ex.getMessage();
    LOGGER.info("Response : 409 {}", error);
    return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
  }

  /**
   * Handle ConstraintViolationException thrown when validation failed.
   *

   * @param ex instance of the exception
   * @return HTTP 422 response with information on invalid fields
   */
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<Map<String, String>> handleValidationExceptions(
      ConstraintViolationException ex) {
    Map<String, String> errors = new HashMap<>();
    ex.getConstraintViolations().forEach(error -> {
      String name = ((PathImpl)error.getPropertyPath()).getLeafNode().getName();
      String errorMessage = error.getMessage();
      errors.put(name, errorMessage);
    });
    LOGGER.info("Response : 422 invalid DTO");
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(errors);
  }

}
