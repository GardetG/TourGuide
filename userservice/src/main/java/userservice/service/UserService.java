package userservice.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import shared.dto.UserDto;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;
import userservice.domain.User;

/**
 * Service Interface to manage users.
 */
@Service
public interface UserService {

  /**
   * Get the user Dto from the username or throw an exception if the user can't be found.
   *
   * @param username of the user
   * @return User Dto
   * @throws UserNotFoundException when user not found
   */
  UserDto getUser(String username) throws UserNotFoundException;

  /**
   * Retrieve the user from the username or throw an exception if the user can't be found.
   *
   * @param username of the user
   * @return User
   * @throws UserNotFoundException when user not found
   */
  User retrieveUser(String username) throws UserNotFoundException;

  /**
   * Add a new User according to the data provided.
   *
   * @param userDto to add
   * @return User dto added
   * @throws UserNameAlreadyUsedException when username is already used
   */
  UserDto addUser(UserDto userDto) throws UserNameAlreadyUsedException;

  /**
   * Get the list of all the users' Id.
   *
   * @return List of Ids
   */
  List<UUID> getAllUserId();

}
