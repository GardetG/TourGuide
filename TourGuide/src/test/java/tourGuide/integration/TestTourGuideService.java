package tourGuide.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.domain.User;
import tourGuide.dto.NearbyAttractionsDto;
import tourGuide.dto.ProviderDto;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.Tracker;

@SpringBootTest(properties = "tourguide.internaluser.internalUserNumber=1")
@ActiveProfiles({"test", "internalUser"})
class TestTourGuideService {

  @Autowired
  private TourGuideService tourGuideService;
  @Autowired
  private Tracker tracker;

  @BeforeEach
  void setUp() {
    tracker.startTracking();
  }

  @AfterEach()
  void tearUp() {
    tracker.stopTracking();
  }

  @BeforeAll
  public static void setDefaultLocale() {
    Locale.setDefault(Locale.UK);
  }

  @Test
  void getUserLocation() throws Exception {
    // Given
    String userName = "internalUser0";
    User user = tourGuideService.getUser(userName);

    // When
    VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);

    // Then
    assertEquals(visitedLocation.userId, user.getUserId());
  }

  @Test
  void getAllUsers() {
    // Given
    String userName = "internalUser0";

    // When
    List<User> allUsers = tourGuideService.getAllUsers();

    // Then
    assertThat(allUsers).hasSize(1);
    assertThat(allUsers.get(0).getUserName()).isEqualTo(userName);
  }

  @Test
  void trackUser() throws Exception {
    // Given
    String userName = "internalUser0";
    User user = tourGuideService.getUser(userName);

    // When
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

    // Then
    assertEquals(visitedLocation.userId, user.getUserId());
  }

  @Test
  void getNearbyAttractions() throws Exception {
    // Given
    String userName = "internalUser0";
    User user = tourGuideService.getUser(userName);
    VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user);

    // When
    NearbyAttractionsDto nearbyAttractionsDto = tourGuideService.getNearByAttractions(userName);

    // Then
    assertThat(nearbyAttractionsDto.getAttractions()).hasSize(5);
  }

  @Test
  void getTripDeals() throws Exception {
    // Given
    String userName = "internalUser0";

    // When
    List<ProviderDto> providers = tourGuideService.getTripDeals(userName);

    // Then
    assertEquals(5, providers.size());
  }

}
