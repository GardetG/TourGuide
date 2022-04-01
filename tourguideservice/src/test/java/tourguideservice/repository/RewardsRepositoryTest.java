package tourguideservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourguideservice.domain.UserReward;

@SpringBootTest(properties = {"tourguide.test.trackingOnStart=false",
    "tourguide.test.useInternalUser=false"})
@ActiveProfiles("test")
class RewardsRepositoryTest {

  @Autowired
  private RewardsRepository rewardsRepository;

  @DisplayName("Find by Id should return list of all user rewards")
  @Test
  void findByIdTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), new Date());
    Attraction attraction = new Attraction("attraction", "", "", 0,0);
    UserReward reward = new UserReward(visitedLocation, attraction, 10);
    rewardsRepository.save(reward);

    // When
    List<UserReward> expectedList = rewardsRepository.findById(userId);

    // Then
    assertThat(expectedList)
        .hasSize(1)
        .containsOnly(reward);
  }

  @DisplayName("Find by Id when user non found should return an empty list")
  @Test
  void findByIdWhenUserNotFoundTest() {
    // Given
    UUID userId = UUID.randomUUID();

    // When
    List<UserReward> expectedList = rewardsRepository.findById(userId);

    // Then
    assertThat(expectedList).isEmpty();
  }

  @DisplayName("Save a reward for a user should add the reward to the Repository")
  @Test
  void saveTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), new Date());
    Attraction attraction = new Attraction("attraction1", "", "", 0,0);
    UserReward reward = new UserReward(visitedLocation, attraction, 10);

    // When
    rewardsRepository.save(reward);

    // Then
    assertThat(rewardsRepository.findById(userId)).containsOnly(reward);
  }

  @DisplayName("Save a reward already saved for a user should not add it to repository")
  @Test
  void saveAlreadySavedTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Attraction attraction = new Attraction("attraction1", "", "", 0,0);
    VisitedLocation visitedLocation1 = new VisitedLocation(userId, new Location(45,-45), new Date());
    VisitedLocation visitedLocation2 = new VisitedLocation(userId, new Location(50,-50), new Date());
    UserReward reward1 = new UserReward(visitedLocation1, attraction, 10);
    rewardsRepository.save(reward1);
    UserReward reward2 = new UserReward(visitedLocation2, attraction, 15);

    // When
    rewardsRepository.save(reward2);

    // Then
    assertThat(rewardsRepository.findById(userId))
        .containsOnly(reward1)
        .doesNotContain(reward2);
  }

}
