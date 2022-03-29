package tourguideservice.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import shared.dto.ProviderDto;
import tourguideservice.domain.User;
import tourguideservice.dto.AttractionDto;
import tourguideservice.dto.LocationDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import tourguideservice.dto.VisitedLocationDto;
import tourguideservice.service.GpsService;
import tourguideservice.service.TourGuideService;
import tourguideservice.tracker.Tracker;

@Tag("integration")
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
    assertThat(nearByAttractions.getUserLocation()).usingRecursiveComparison().isEqualTo(location);
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
    gpsService.addLocation(new VisitedLocationDto(
        user.getUserId(),
        new LocationDto(attraction.getLongitude(), attraction.getLatitude()),
        new Date()
    ));

    // When
    tourGuideService.calculateRewards(user.getUserId());

    // Then
    assertThat(tourGuideService.getUserRewards(userName)).hasSize(1);
  }

}
