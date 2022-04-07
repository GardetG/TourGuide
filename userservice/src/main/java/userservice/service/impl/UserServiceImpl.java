package userservice.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shared.dto.UserDto;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;
import userservice.domain.User;
import userservice.repository.UserRepository;
import userservice.service.UserService;
import userservice.utils.UserMapper;

/**
 * Service Class implementation to manage users' preferences.
 */
@Service
public class UserServiceImpl implements UserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDto getUser(String username) throws UserNotFoundException {
    User user = userRepository.findByUsername(username)
        .orElseThrow(() -> {
          LOGGER.error("User {} not found", username);
          return new UserNotFoundException("User not found");
        });
    return UserMapper.toDto(user);
  }

  @Override
  public UserDto addUser(UserDto userDto) throws UserNameAlreadyUsedException {
    if (userRepository.findByUsername(userDto.getUserName()).isPresent()) {
      LOGGER.error("Username {} already used", userDto.getUserName());
      throw new UserNameAlreadyUsedException("This username is already used");
    }
    User user = userRepository.save(UserMapper.toEntity(userDto));
    return UserMapper.toDto(user);
  }

  @Override
  public List<UUID> getAllUserId() {
    return userRepository.findAll()
        .stream()
        .map(User::getUserId)
        .collect(Collectors.toList());
  }
}
