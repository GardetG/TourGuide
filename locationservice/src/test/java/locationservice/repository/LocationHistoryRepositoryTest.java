package locationservice.repository;

import static org.assertj.core.api.Assertions.assertThat;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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

  @BeforeEach
  private void setUp() {
    locationHistoryRepository.deleteAll();
  }

  @DisplayName("Find all should return list of all visited locations")
  @Test
  void findAll() {
    // Given
    VisitedLocation visitedLocation1 = new VisitedLocation(UUID.randomUUID(), new Location(45,-45), new Date());
    VisitedLocation visitedLocation2 = new VisitedLocation(UUID.randomUUID(), new Location(45,-45), new Date());
    locationHistoryRepository.save(visitedLocation1);
    locationHistoryRepository.save(visitedLocation2);

    // When
    List<VisitedLocation> expectedList = locationHistoryRepository.findAll();

    // Then
    assertThat(expectedList)
        .hasSize(2)
        .containsOnly(visitedLocation1, visitedLocation2);
  }


  @DisplayName("Find all when no visited location persisted should return an empty list")
  @Test
  void findAllWhenEmpty() {
    // When
    List<VisitedLocation> expectedList = locationHistoryRepository.findAll();

    // Then
    assertThat(expectedList).isEmpty();
  }

  @DisplayName("Find by Id should return list of all the user's visited locations")
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

  @DisplayName("Find by Id when user not found should return an empty list")
  @Test
  void findByIdWhenUserNotFoundTest() {
    // Given
    UUID userId = UUID.randomUUID();

    // When
    List<VisitedLocation> expectedList = locationHistoryRepository.findById(userId);

    // Then
    assertThat(expectedList).isEmpty();
  }

  @DisplayName("Save visited location for user should persist the visited location")
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

}
