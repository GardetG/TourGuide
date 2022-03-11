package tourGuide.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.domain.User;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionsListDto;
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
    Location expectedLocation = user.getLastVisitedLocation().location;

    // When
    LocationDto location = tourGuideService.getUserLocation("internalUser0");

    // Then
    assertThat(location).isEqualToComparingFieldByField(expectedLocation);
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
    NearbyAttractionsListDto nearbyAttractionsDto = tourGuideService.getNearByAttractions(userName);

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
