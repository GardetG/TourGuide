package tourguideservice.exception;

/**
 * Exception thrown when no visited location found for the requested user.
 */
public class NoLocationFoundException extends Exception {

  public NoLocationFoundException(String s) {
    super(s);
  }

}
