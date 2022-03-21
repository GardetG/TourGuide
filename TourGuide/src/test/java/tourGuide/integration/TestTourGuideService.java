package tourGuide.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.domain.User;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionsListDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.service.GpsService;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.Tracker;

@SpringBootTest(properties = "tourguide.internaluser.internalUserNumber=1")
@ActiveProfiles({"test", "internalUser"})
class TestTourGuideService {

  @Autowired
  private TourGuideService tourGuideService;
  @Autowired
  private GpsService gpsService;
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

    // When
    LocationDto location = tourGuideService.getUserLocation("internalUser0");

    // Then
    assertThat(location).isNotNull();
  }

  @Test
  void getAllCurrentLocations() {
    // Given
    String userName = "internalUser0";

    // When
    Map<UUID, LocationDto> locationMap = tourGuideService.getAllCurrentLocations();

    // Then
    assertThat(locationMap).isNotEmpty();
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

  @Test
  void getNearByAttractions() throws Exception {
    // Given
    String userName = "internalUser0";
    LocationDto location = tourGuideService.getUserLocation(userName);

    // When
    NearbyAttractionsListDto nearByAttractions = tourGuideService.getNearByAttractions(userName);

    // Then
    assertThat(nearByAttractions.getUserLocation()).isEqualToComparingFieldByField(location);
    assertThat(nearByAttractions.getAttractions()).hasSize(5);
  }

  @Test
  void trackUser() throws Exception {
    // Given
    String userName = "internalUser0";
    User user = tourGuideService.getUser(userName);

    // When
    VisitedLocationDto visitedLocation = tourGuideService.trackUserLocation(user.getUserId());

    // Then
    assertEquals(visitedLocation.getUserId(), user.getUserId());
  }

  @Test
  void calculateRewards() throws Exception {
    // Given
    String userName = "internalUser0";
    User user = tourGuideService.getUser(userName);
    AttractionDto attraction = gpsService.getAttraction().get(0);
    gpsService.addLocation(user.getUserId(), new LocationDto(attraction.getLongitude(), attraction.getLatitude()));

    // When
    tourGuideService.calculateRewards(user.getUserId());

    // Then
    assertThat(tourGuideService.getUserRewards(userName)).hasSize(1);
  }

}
