package shared.exception;

/**
 * Exception thrown when the username of a User to persist is already used.
 */
public class UserNameAlreadyUsedException extends Exception {

  public UserNameAlreadyUsedException(String s) {
    super(s);
  }

}
