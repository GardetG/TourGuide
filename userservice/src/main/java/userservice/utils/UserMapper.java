package userservice.utils;

import shared.dto.UserDto;
import userservice.domain.User;

/**
 * Mapper utility class to map User DTO and entity.
 */
public class UserMapper {

  private UserMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a User entity into DTO.
   *
   * @param user to map
   * @return corresponding UserDto mapped
   */
  public static UserDto toDto(User user) {
    return new UserDto(
        user.getUserId(),
        user.getUserName(),
        user.getPhoneNumber(),
        user.getEmailAddress()
    );
  }

  /**
   * Map a User DTO into entity.
   *
   * @param userDto to map
   * @return corresponding User mapped
   */
  public static User toEntity(UserDto userDto) {
    return new User(
        userDto.getUserName(),
        userDto.getPhoneNumber(),
        userDto.getEmailAddress()
    );
  }

}
