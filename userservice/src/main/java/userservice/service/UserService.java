package userservice.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import shared.dto.UserDto;
import shared.exception.UserNotFoundException;

/**
 * Service Interface to manage users.
 */
@Service
public interface UserService {

  /**
   * Get the user from the username or throw an exception if the user can't be found.
   *
   * @param username of the user
   * @return User id of the user
   * @throws UserNotFoundException when user not found
   */
  UserDto getUser(String username) throws UserNotFoundException;


  /**
   * Add a new User according to the data provided.
   *
   * @param userDto to add
   * @return User dto added
   */
  UserDto addUser(UserDto userDto);

  /**
   * Get the list of all the users' Id.
   *
   * @return List of Ids
   */
  List<UUID> getAllUserId();

}
