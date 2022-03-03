package tourGuide.exception;

/**
 * Exception thrown when the requested user is not found.
 */
public class UserNotFoundException extends Exception {

  public UserNotFoundException(String s) {
    super(s);
  }

}
