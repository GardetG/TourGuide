package tourguideservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourguideservice.domain.User;

@SpringBootTest(properties = {"tourguide.test.trackingOnStart=false",
    "tourguide.test.useInternalUser=false"})
@ActiveProfiles("test")
class UserRepositoryTest {

  @Autowired
  private UserRepository userRepository;

  @DisplayName("Save User should add the User to the Repository")
  @Test
  void saveTest() {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    // When
    userRepository.save(user);

    // Then
    assertThat(userRepository.findAll()).containsOnly(user);
  }

  @DisplayName("Save User with already existing userName should not add it to the Repository")
  @Test
  void saveExistingUserNameTest() {
    // Given
    User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    userRepository.save(user1);
    User user2 = new User(UUID.randomUUID(), "jon", "002", "jon2@tourGuide.com");

    // When
    userRepository.save(user2);

    // Then
    assertThat(userRepository.findAll()).doesNotContain(user2);
  }

  @DisplayName("Finding by userName should return the user if they exists")
  @Test
  void findByUserName() {
    // Given
    User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    userRepository.save(user1);

    // When
    Optional<User> actualUser = userRepository.findByUsername("jon");

    // Then
    assertThat(actualUser)
        .isPresent()
        .contains(user1);
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
    User user1 = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    userRepository.save(user1);

    // When
    userRepository.deleteAll();

    // Then
    assertThat(userRepository.findAll()).isEmpty();
  }

}
