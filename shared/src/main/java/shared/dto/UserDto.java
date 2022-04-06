package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Dto Class for User entity.
 */
public class UserDto {

  private final UUID userId;
  private final String userName;
  private final String phoneNumber;
  private final String emailAddress;

  /**
   * Constructor for an instance of UserDto with userId, name and personal coordinate.
   * the trip.
   *
   * @param userId       of the user
   * @param userName     of the user
   * @param phoneNumber  of the user
   * @param emailAddress of the user
   */
  @JsonCreator
  public UserDto(@JsonProperty("userId") UUID userId,
                 @JsonProperty("userName") String userName,
                 @JsonProperty("phoneNumber") String phoneNumber,
                 @JsonProperty("emailAddress") String emailAddress) {
    this.userId = userId;
    this.userName = userName;
    this.phoneNumber = phoneNumber;
    this.emailAddress = emailAddress;
  }

  public UUID getUserId() {
    return userId;
  }

  public String getUserName() {
    return userName;
  }

  public String getPhoneNumber() {
    return phoneNumber;
  }

  public String getEmailAddress() {
    return emailAddress;
  }

}
