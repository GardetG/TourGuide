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
import javax.annotation.meta.When;
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


  @DisplayName("Get attractions should return list of attraction Dto")
  @Test
  void getAttractionTest() {
    // Given
    Attraction attraction = new Attraction("attraction", "","",45,-45);
    AttractionDto attractionDto = new AttractionDto(attraction.attractionId,-45,45,"attraction","","");
    when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction));

    // When
    List<AttractionDto> attractions = gpsService.getAttraction();

    // Then
    assertThat(attractions).usingRecursiveFieldByFieldElementComparator().containsExactly(attractionDto);
    verify(gpsUtil, times(1)).getAttractions();
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

  @DisplayName("Get visited locations should return map of attractions and first in range visited location")
  @Test
  void getVisitedAttractionsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation location1 = new VisitedLocation(userId, new Location(0,0), new Date());
    VisitedLocation location2 = new VisitedLocation(userId, new Location(0,0), new Date());
    VisitedLocationDto locationDto = new VisitedLocationDto(userId, new LocationDto(0,0), location1.timeVisited);
    Attraction attraction1 = new Attraction("attraction1", "","",0,0);
    AttractionDto attractionDto1 = new AttractionDto(attraction1.attractionId,0,0,"attraction1","","");
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(Arrays.asList(location1, location2));
    when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction1));

    // When
    Map<AttractionDto, VisitedLocationDto> visitedAttractions = gpsService.getVisitedAttractions(userId);

    // Then
    assertThat(visitedAttractions).hasSize(1);
    assertThat(visitedAttractions.keySet()).usingRecursiveFieldByFieldElementComparator()
        .containsOnly(attractionDto1);
    assertThat(visitedAttractions.values()).usingRecursiveFieldByFieldElementComparator()
        .containsOnly(locationDto);
    verify(locationHistoryRepository, times(1)).findById(userId);
    verify(gpsUtil, times(1)).getAttractions();
  }

  @DisplayName("Get visited locations when no visited location in range should return an empty map")
  @Test
  void getVisitedAttractionsWhenNoLocationInRangeTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation location = new VisitedLocation(userId, new Location(90,0), new Date());
    Attraction attraction = new Attraction("attraction1", "","",0,0);
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(Collections.singletonList(location));
    when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction));

    // When
    Map<AttractionDto, VisitedLocationDto> visitedAttractions = gpsService.getVisitedAttractions(userId);

    // Then
    assertThat(visitedAttractions).isEmpty();
    verify(locationHistoryRepository, times(1)).findById(userId);
    verify(gpsUtil, times(1)).getAttractions();
  }

  @DisplayName("Get visited location without registered locations should return an empty map")
  @Test
  void getVisitedAttractionsWhenNoLocationsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Attraction attraction = new Attraction("test", "","",90,-90);
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(new ArrayList<>());
    when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction));

    // When
    Map<AttractionDto, VisitedLocationDto> visitedAttractions = gpsService.getVisitedAttractions(userId);

    // Then
    assertThat(visitedAttractions).isEmpty();
    verify(locationHistoryRepository, times(1)).findById(userId);
    verify(gpsUtil, times(1)).getAttractions();
  }

  @DisplayName("Get nearby attractions with distances should return nearest attractions with distances")
  @Test
  void getNearbyAttractionsTest() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation location = new VisitedLocation(userId, new Location(0, 0), new Date());
    int limit = 2;
    Attraction attraction1 = new Attraction("attraction1", "", "", 0, -90);
    Attraction attraction2 = new Attraction("attraction2", "", "", 0, 0);
    Attraction attraction3 = new Attraction("attraction3", "", "", 0, 45);
    AttractionDto expectedAttraction1 = new AttractionDto(attraction2.attractionId, 0,0, "attraction2", "","");
    AttractionDto expectedAttraction2 = new AttractionDto(attraction3.attractionId, 45,0, "attraction3", "","");
    when(locationHistoryRepository.findFirstByIdOrderByDateDesc(any(UUID.class))).thenReturn(Optional.of(location));
    when(gpsUtil.getAttractions()).thenReturn(Arrays.asList(attraction1, attraction2, attraction3));

    // When
    Map<AttractionDto, Double> attractionsWithDistance = gpsService.getNearbyAttractions(userId, limit);

    // Then
    assertThat(attractionsWithDistance)
        .hasSize(limit)
        .containsValues(0d, 2700d * TourGuideProperties.STATUTE_MILES_PER_NAUTICAL_MILE);
    assertThat(attractionsWithDistance.keySet())
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedAttraction1, expectedAttraction2);
    verify(locationHistoryRepository, times(1)).findFirstByIdOrderByDateDesc(userId);
    verify(gpsUtil, times(1)).getAttractions();
  }

  @DisplayName("Get nearby attractions when no location registered should throw an exception")
  @Test
  void getNearbyAttractionsWhenEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    int limit = 2;
    when(locationHistoryRepository.findFirstByIdOrderByDateDesc(any(UUID.class))).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> gpsService.getNearbyAttractions(userId,limit))
        .isInstanceOf(NoLocationFoundException.class)
        .hasMessageContaining("No location registered for the user yet");
    verify(locationHistoryRepository, times(1)).findFirstByIdOrderByDateDesc(userId);
    verify(gpsUtil, times(0)).getAttractions();
  }

  @DisplayName("Get nearby attractions when limit negative should throw an exception")
  @Test
  void getNearbyAttractionsWhenInvalidLimitTest() {
    // Given
    UUID userId = UUID.randomUUID();
    int limit = -1;

    // Then
    assertThatThrownBy(() -> gpsService.getNearbyAttractions(userId,limit))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Limit must be positive");
    verify(locationHistoryRepository, times(0)).findFirstByIdOrderByDateDesc(any(UUID.class));
    verify(gpsUtil, times(0)).getAttractions();
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
