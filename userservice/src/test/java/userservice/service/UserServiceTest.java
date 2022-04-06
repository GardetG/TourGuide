package userservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import shared.dto.UserDto;
import shared.exception.NoLocationFoundException;
import shared.exception.UserNameAlreadyUsedException;
import shared.exception.UserNotFoundException;
import userservice.domain.User;
import userservice.repository.UserRepository;

@SpringBootTest
class UserServiceTest {

  @Autowired
  private UserService userService;

  @MockBean
  private UserRepository userRepository;

  @Captor
  ArgumentCaptor<User> userCaptor;

  @DisplayName("Get user by username should return a user Dto")
  @Test
  void getUserTest() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    User user = User.of(new User("jon", "000-000-0000", "jon@test.com"), userId);
    UserDto expectedUser = new UserDto(userId, "jon", "000-000-0000", "jon@test.com");
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // When
    UserDto actualUser = userService.getUser("jon");

    // Then
    assertThat(actualUser).usingRecursiveComparison().isEqualTo(expectedUser);
    verify(userRepository, times(1)).findByUsername("jon");
  }

  @DisplayName("Get user when not found should throw an exception")
  @Test
  void getUserNotFoundTest() {
    // Given
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> userService.getUser("NonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("NonExistent");
  }

  @DisplayName("Get all users' id should return a list of id")
  @Test
  void getAllUserIdTest() {
    // Given
    UUID user1Id = UUID.randomUUID();
    User user1 = User.of(new User("jon", "000-000-0001", "jon@test.com"), user1Id);
    UUID user2id = UUID.randomUUID();
    User user2 = User.of(new User("jane", "000-000-0002", "jane@test.com"), user2id);
    when(userRepository.findAll()).thenReturn(List.of(user1, user2));

    // When
    List<UUID> actualIds = userService.getAllUserId();

    // Then
    assertThat(actualIds).containsExactly(user1Id, user2id);
    verify(userRepository, times(1)).findAll();
  }

  @DisplayName("Get all users' id when empty should return an empty list")
  @Test
  void getAllUserIdWhenEmptyTest() {
    // Given
    when(userRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<UUID> actualIds = userService.getAllUserId();

    // Then
    assertThat(actualIds).isEmpty();
    verify(userRepository, times(1)).findAll();
  }


  @DisplayName("Add user should persist User and return it")
  @Test
  void addUserTest() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    User user = User.of(new User("jon", "000-000-0000", "jon@test.com"), userId);
    UserDto userDto = new UserDto(userId, "jon", "000-000-0000", "jon@test.com");
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());
    when(userRepository.save(any(User.class))).thenReturn(user);

    // When
    UserDto actualDto = userService.addUser(userDto);

    // Then
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(userDto);
    verify(userRepository, times(1)).save(userCaptor.capture());
    assertThat(userCaptor.getValue()).usingRecursiveComparison().ignoringFields("userId").isEqualTo(user);
  }

  @DisplayName("Add user with already used username should throw an Exception")
  @Test
  void addUserWithAlreadyUsedUserNameTest() {
    // Given
    UUID userId = UUID.randomUUID();
    User user = User.of(new User("jon", "000-000-0000", "jon@test.com"), userId);
    UserDto userDto = new UserDto(userId, "jon", "000-000-0000", "jon@test.com");
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // Then
    assertThatThrownBy(() -> userService.addUser(userDto))
        .isInstanceOf(UserNameAlreadyUsedException.class)
        .hasMessageContaining("This username is already used");
    verify(userRepository, times(1)).findByUsername("jon");
    verify(userRepository, times(0)).save(any(User.class));
  }

}
