package tourGuide.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.config.TourGuideProperties;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.LocationDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.exception.NoLocationFoundException;
import tourGuide.repository.LocationHistoryRepository;

@SpringBootTest
@ActiveProfiles("test")
class GpsServiceTest {

  @Autowired
  private GpsService gpsService;

  @MockBean
  private GpsUtil gpsUtil;
  @MockBean
  private LocationHistoryRepository locationHistoryRepository;

  @Captor
  ArgumentCaptor<VisitedLocation> visitedLocationCaptor;

  @DisplayName("Get attractions with distances should return a map of attractions and distances")
  @Test
  void getAttractionsWithDistancesTest() {
    // Given
    Location location = new Location(0, 0);
    Attraction attraction1 = new Attraction("attraction1", "", "", 0, 0);
    Attraction attraction2 = new Attraction("attraction2", "", "", 0, 45);
    when(gpsUtil.getAttractions()).thenReturn(Arrays.asList(attraction1, attraction2));

    // When
    Map<Attraction, Double> attractionsWithDistance = gpsService.getAttractionsWithDistances(location);

    // Then
    assertThat(attractionsWithDistance)
        .hasSize(2)
        .containsOnlyKeys(attraction1, attraction2)
        .containsValues(0d, 2700d * TourGuideProperties.STATUTE_MILES_PER_NAUTICAL_MILE);
  }

  @DisplayName("Get Top nearby attractions with distances should return top nearest attractions with distances")
  @Test
  void getTopNearbyAttractionsWithDistancesTest() {
    // Given
    Location location = new Location(0, 0);
    int limit = 2;
    Attraction attraction1 = new Attraction("attraction2", "", "", 0, -90);
    Attraction attraction2 = new Attraction("attraction1", "", "", 0, 0);
    Attraction attraction3 = new Attraction("attraction2", "", "", 0, 45);
    when(gpsUtil.getAttractions()).thenReturn(Arrays.asList(attraction1, attraction2, attraction3));

    // When
    Map<Attraction, Double> attractionsWithDistance = gpsService.getTopNearbyAttractionsWithDistances(location, limit);

    // Then
    assertThat(attractionsWithDistance)
        .hasSize(limit)
        .containsOnlyKeys(attraction2, attraction3)
        .containsValues(0d, 2700d * TourGuideProperties.STATUTE_MILES_PER_NAUTICAL_MILE);
  }

  @DisplayName("Get last location should return last location Dto")
  @Test
  void getLastLocationTest() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), date);
    VisitedLocationDto expectedLocation = new VisitedLocationDto(userId, new LocationDto(-45,45), date);
    when(locationHistoryRepository.findFirstByIdOrderByDateDesc(any(UUID.class)))
        .thenReturn(Optional.of(visitedLocation));

    // When
    VisitedLocationDto actualLocation = gpsService.getLastLocation(userId);

    // Then
    assertThat(actualLocation).isEqualToComparingFieldByFieldRecursively(expectedLocation);
    verify(locationHistoryRepository, times(1)).findFirstByIdOrderByDateDesc(userId);
  }

  @DisplayName("Get last location when no location registered should throw an exception")
  @Test
  void getLastLocationWhenEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    when(locationHistoryRepository.findFirstByIdOrderByDateDesc(any(UUID.class)))
        .thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> gpsService.getLastLocation(userId))
        .isInstanceOf(NoLocationFoundException.class)
        .hasMessageContaining("No location registered for the user yet");
    verify(locationHistoryRepository, times(1)).findFirstByIdOrderByDateDesc(userId);
  }

  @DisplayName("Track user location should registered and return current visited location")
  @Test
  void trackUserLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), date);
    VisitedLocationDto expectedLocation = new VisitedLocationDto(userId, new LocationDto(-45,45), date);
    when(gpsUtil.getUserLocation(any(UUID.class))).thenReturn(visitedLocation);

    // When
    VisitedLocationDto actualLocation = gpsService.trackUserLocation(userId);

    // Then
    assertThat(actualLocation).isEqualToComparingFieldByFieldRecursively(expectedLocation);
    verify(gpsUtil, times(1)).getUserLocation(userId);
    verify(locationHistoryRepository, times(1)).save(visitedLocationCaptor.capture());
    assertThat(visitedLocationCaptor.getValue()).isEqualTo(visitedLocation);
  }

  @DisplayName("Add location to user should save visited location in repository")
  @Test
  void addLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    LocationDto location = new LocationDto(45, -45);

    // When
    gpsService.addLocation(userId, location);

    // Then
    verify(locationHistoryRepository, times(1)).save(visitedLocationCaptor.capture());
    assertThat(visitedLocationCaptor.getValue().location).isEqualToComparingFieldByFieldRecursively(location);
    assertThat(visitedLocationCaptor.getValue().userId).isEqualTo(userId);
  }

  @DisplayName("Get distance between two locations should return distance in miles")
  @ParameterizedTest(name = "Distance between 0,0 and {0},{1} equals {2} nmi")
  @CsvSource({"0,0,0", "0,45,2700", "0,-45,2700", "0,90,5400", "45,0,2700", "-45,0,2700",
      "90,0,5400"})
  void getDistance(double lat, double lon, double distance) {
    // Given
    Location locationRef = new Location(0, 0);
    Location location = new Location(lat, lon);

    // When
    double actualDistance = gpsService.getDistance(location, locationRef);

    // Then
    assertThat(actualDistance)
        .isEqualTo(gpsService.getDistance(locationRef, location))
        .isEqualTo(distance * TourGuideProperties.STATUTE_MILES_PER_NAUTICAL_MILE);
  }

  @DisplayName("Get user location should return user location from GpsUtil library")
  @Test
  void getUserLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(0, 0), new Date());
    when(gpsUtil.getUserLocation(any(UUID.class))).thenReturn(visitedLocation);

    // When
    VisitedLocation actualLocation = gpsService.getUserLocation(userId);

    // Then
    assertThat(actualLocation).isEqualTo(visitedLocation);
    verify(gpsUtil, times(1)).getUserLocation(userId);
  }

}
