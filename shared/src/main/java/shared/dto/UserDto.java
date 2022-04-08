package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;
import javax.validation.constraints.NotBlank;

/**
 * Dto Class for User entity.
 */
public class UserDto {

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

  private final UUID userId;
  @NotBlank(message = "UserName is mandatory")
  private final String userName;
  @NotBlank(message = "Phone number is mandatory")
  private final String phoneNumber;
  @NotBlank(message = "Email address is mandatory")
  private final String emailAddress;

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
