package tourguideservice.exception;

/**
 * Exception thrown when Feign client proxy return an unexpected error.
 */
public class ProxyResponseErrorException extends RuntimeException {

  public ProxyResponseErrorException(String s) {
    super(s);
  }

}