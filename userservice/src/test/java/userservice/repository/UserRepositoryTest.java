package userservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import userservice.domain.User;

@SpringBootTest
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @BeforeEach
  private void setUp() {
    userRepository.deleteAll();
  }

  @DisplayName("Save User should add the User to the Repository")
  @Test
  void saveTest() {
    // Given
    User user = new User("jon", "000", "jon@tourGuide.com");

    // When
    User actualUser = userRepository.save(user);

    // Then
    assertThat(actualUser).usingRecursiveComparison()
        .ignoringFields("userId").isEqualTo(user);
    assertThat(actualUser.getUserId()).isNotNull();
    assertThat(userRepository.findAll())
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("userId").containsOnly(user);
  }

  @DisplayName("Save User with already existing userName should not add it to the Repository")
  @Test
  void saveExistingUserNameTest() {
    // Given
    User user1 = new User("jon", "000", "jon@tourGuide.com");
    userRepository.save(user1);
    User user2 = new User("jon", "002", "jon2@tourGuide.com");

    // When
    User actualUser = userRepository.save(user2);

    // Then
    assertThat(actualUser).isNull();
    assertThat(userRepository.findAll())
        .usingRecursiveFieldByFieldElementComparatorIgnoringFields("userId").containsOnly(user1);
  }

  @DisplayName("Finding by userName should return the user if they exists")
  @Test
  void findByUserName() {
    // Given
    User user1 = new User("jon", "000", "jon@tourGuide.com");
    userRepository.save(user1);

    // When
    Optional<User> actualUser = userRepository.findByUsername("jon");

    // Then
    assertThat(actualUser).isPresent();
    assertThat(actualUser).get().usingRecursiveComparison().ignoringFields("userId").isEqualTo(user1);
  }

  @DisplayName("Finding by userName should return an empty optional if they don't exists")
  @Test
  void findByUserNotFoundNameTest() {
    // When
    Optional<User> actualUser = userRepository.findByUsername("nonExistent");

    // Then
    assertThat(actualUser).isEmpty();
  }

  @DisplayName("Finding by userName should return an empty optional if they don't exists")
  @Test
  void deleteAllTest() {
    // Given
    User user1 = new User("jon", "000", "jon@tourGuide.com");
    userRepository.save(user1);

    // When
    userRepository.deleteAll();

    // Then
    assertThat(userRepository.findAll()).isEmpty();
  }

}
