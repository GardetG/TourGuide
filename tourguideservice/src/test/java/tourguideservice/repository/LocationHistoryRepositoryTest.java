package tourguideservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class LocationHistoryRepositoryTest {

  @Autowired
  private LocationHistoryRepository locationHistoryRepository;

  @DisplayName("Find by Id should return list of all user visited locations")
  @Test
  void findByIdTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), new Date());
    locationHistoryRepository.save(visitedLocation);

    // When
    List<VisitedLocation> expectedList = locationHistoryRepository.findById(userId);

    // Then
    assertThat(expectedList)
        .hasSize(1)
        .containsOnly(visitedLocation);
  }

  @DisplayName("Find by Id when user non found should return an empty list")
  @Test
  void findByIdWhenUserNotFoundTest() {
    // Given
    UUID userId = UUID.randomUUID();

    // When
    List<VisitedLocation> expectedList = locationHistoryRepository.findById(userId);

    // Then
    assertThat(expectedList).isEmpty();
  }

  @DisplayName("Save visited location for user should add the visited location to the Repository")
  @Test
  void saveTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), new Date());

    // When
    locationHistoryRepository.save(visitedLocation);

    // Then
    assertThat(locationHistoryRepository.findById(userId)).containsOnly(visitedLocation);
  }

  @DisplayName("Finding last visited location should return the most recent visited location")
  @Test
  void findFirstByIdOrderByDateDescTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation firstLocation = new VisitedLocation(userId, new Location(45,-45), new Date(0));
    VisitedLocation lastLocation = new VisitedLocation(userId, new Location(45,-45), new Date(200));
    VisitedLocation location = new VisitedLocation(userId, new Location(45,-45), new Date(100));
    locationHistoryRepository.save(firstLocation);
    locationHistoryRepository.save(lastLocation);
    locationHistoryRepository.save(location);

    // When
    Optional<VisitedLocation> actualVisitedLocation = locationHistoryRepository.findFirstByIdOrderByDateDesc(userId);

    // Then
    assertThat(actualVisitedLocation)
        .isPresent()
        .contains(lastLocation);
  }

  @DisplayName("Finding last visited location when user not found should return an empty optional")
  @Test
  void findFirstByIdOrderByDateDescWhenUserNotFoundTest() {
    // Given
    UUID userId = UUID.randomUUID();

    // When
    Optional<VisitedLocation> actualVisitedLocation = locationHistoryRepository.findFirstByIdOrderByDateDesc(userId);

    // Then
    assertThat(actualVisitedLocation).isEmpty();
  }

}
