package userservice.controller;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.dto.UserDto;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;
import userservice.service.PreferencesService;
import userservice.service.UserService;

/**
 * Controller Class exposing UserService API end points.
 */
@RestController
public class UserController {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

  @Autowired
  private UserService userService;
  @Autowired
  private PreferencesService preferencesService;

  /**
   * GET the user associated with the provided username and return HTTP 200 with user Dto or throw
   * an exception if the user is not found.
   *
   * @param userName of the user
   * @return HTTP 200 with user dto
   * @throws UserNotFoundException if user not found.
   */
  @GetMapping("/getUser")
  public UserDto getTripDeals(@RequestParam String userName) throws UserNotFoundException {
    LOGGER.info("Request: Get user with username {}", userName);
    UserDto userDto = userService.getUser(userName);
    LOGGER.info("Response: User {} associated with username {} sent", userDto.getUserId(), userName);
    return userDto;
  }

  /**
   * POST a new user to registered inside the service and return HTTP 200 with the created User Dto
   * or throw an exception if the userName is already used.
   *
   * @param userDto to add
   * @return HTTP 200 User added Dto
   * @throws UserNameAlreadyUsedException if username already used.
   */
  @PostMapping("/addUser")
  public UserDto getTripDeals(@RequestBody @Valid UserDto userDto) throws UserNameAlreadyUsedException {
    LOGGER.info("Request: Registered new user with username {}", userDto.getUserName());
    UserDto userAddedDto = userService.addUser(userDto);
    LOGGER.info("Response: User {} associated with username {} registred", userAddedDto.getUserId(), userAddedDto.getUserName());
    return userAddedDto;
  }

  /**
   * GET the list of all registered users' id and return HTTP 200 with the list.
   *
   * @return HTTP 200 with UUID list
   */
  @GetMapping("/getAllUserId")
  public List<UUID> getTripDeals() {
    LOGGER.info("Request: Get list of all registered users' id");
    List<UUID> usersId = userService.getAllUserId();
    LOGGER.info("Response: List of all registered users' id sent");
    return usersId;
  }

}
