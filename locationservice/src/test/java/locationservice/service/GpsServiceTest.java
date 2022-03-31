package locationservice.service;

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
import java.util.UUID;
import locationservice.config.LocationServiceProperties;
import locationservice.repository.LocationHistoryRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import shared.dto.AttractionDto;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.LocationDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

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

  @DisplayName("Get All last location should return list of all users' last location Dto")
  @Test
  void getAllLastLocationTest() {
    // Given
    UUID user1Id = UUID.randomUUID();
    UUID user2Id = UUID.randomUUID();
    VisitedLocation oldVisitedLocation1 = new VisitedLocation(user1Id, new Location(90,-90), new Date());
    Date date = new Date();
    VisitedLocation visitedLocation1 = new VisitedLocation(user1Id, new Location(45,-45), date);
    VisitedLocationDto expectedLocation1 = new VisitedLocationDto(user1Id, new LocationDto(45,-45), date);
    VisitedLocation visitedLocation2 = new VisitedLocation(user2Id, new Location(45,-45), date);
    VisitedLocationDto expectedLocation2 = new VisitedLocationDto(user2Id, new LocationDto(45,-45), date);
    when(locationHistoryRepository.findAll()).thenReturn(List.of(visitedLocation1, oldVisitedLocation1, visitedLocation2));

    // When
    List<VisitedLocationDto> actualLocations = gpsService.getAllUserLastVisitedLocation();

    // Then
    assertThat(actualLocations).usingRecursiveFieldByFieldElementComparator()
        .containsOnly(expectedLocation1, expectedLocation2);
    verify(locationHistoryRepository, times(1)).findAll();
  }

  @DisplayName("Get all users' last location when no location registered should return an empty list")
  @Test
  void getAllLastLocationWhenEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    when(locationHistoryRepository.findAll()).thenReturn(Collections.emptyList());

    // When
    List<VisitedLocationDto> actualLocations = gpsService.getAllUserLastVisitedLocation();

    // Then
    assertThat(actualLocations).isEmpty();
    verify(locationHistoryRepository, times(1)).findAll();
  }

  @DisplayName("Get last location should return last location Dto")
  @Test
  void getLastLocationTest() throws Exception {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocation oldVisitedLocation = new VisitedLocation(userId, new Location(90,-90), new Date());
    Date date = new Date();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), date);
    VisitedLocationDto expectedLocation = new VisitedLocationDto(userId, new LocationDto(45,-45), date);
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(List.of(visitedLocation, oldVisitedLocation));

    // When
    VisitedLocationDto actualLocation = gpsService.getUserLastVisitedLocation(userId);

    // Then
    assertThat(actualLocation).usingRecursiveComparison().isEqualTo(expectedLocation);
    verify(locationHistoryRepository, times(1)).findById(userId);
  }

  @DisplayName("Get last location when no location registered should throw an exception")
  @Test
  void getLastLocationWhenEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(Collections.emptyList());

    // Then
    assertThatThrownBy(() -> gpsService.getUserLastVisitedLocation(userId))
        .isInstanceOf(NoLocationFoundException.class)
        .hasMessageContaining("No location registered for the user yet");
    verify(locationHistoryRepository, times(1)).findById(userId);
  }

  @DisplayName("Track user location should registered and return current visited location")
  @Test
  void trackUserLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), date);
    VisitedLocationDto expectedLocation = new VisitedLocationDto(userId, new LocationDto(45,-45), date);
    when(gpsUtil.getUserLocation(any(UUID.class))).thenReturn(visitedLocation);

    // When
    VisitedLocationDto actualLocation = gpsService.trackUserLocation(userId);

    // Then
    assertThat(actualLocation).usingRecursiveComparison().isEqualTo(expectedLocation);
    verify(gpsUtil, times(1)).getUserLocation(userId);
    verify(locationHistoryRepository, times(1)).save(visitedLocationCaptor.capture());
    assertThat(visitedLocationCaptor.getValue()).isEqualTo(visitedLocation);
  }


  @DisplayName("Get attractions should return list of attraction Dto")
  @Test
  void getAttractionsTest() {
    // Given
    Attraction attraction = new Attraction("attraction", "","",45,-45);
    AttractionDto attractionDto = new AttractionDto(attraction.attractionId,"attraction","","", 45,-45);
    when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction));

    // When
    List<AttractionDto> attractions = gpsService.getAttractions();

    // Then
    assertThat(attractions).usingRecursiveFieldByFieldElementComparator().containsExactly(attractionDto);
    verify(gpsUtil, times(1)).getAttractions();
  }

  @DisplayName("Add location to user should save visited location in repository")
  @Test
  void addLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocationDto locationDto = new VisitedLocationDto(userId, new LocationDto(-45, 45), date);
    VisitedLocation expectedLocation = new VisitedLocation(userId, new Location(-45,45), date);

    // When
    gpsService.addVisitedLocation(userId, List.of(locationDto));

    // Then
    verify(locationHistoryRepository, times(1)).save(visitedLocationCaptor.capture());
    assertThat(visitedLocationCaptor.getValue()).usingRecursiveComparison().isEqualTo(expectedLocation);
  }

  @DisplayName("Add location to user should save visited location in repository")
  @Test
  void addLocationWhenIdMismatchTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    VisitedLocationDto locationDto = new VisitedLocationDto(UUID.randomUUID(), new LocationDto(-45, 45), date);
    VisitedLocation expectedLocation = new VisitedLocation(userId, new Location(-45,45), date);

    // When
    gpsService.addVisitedLocation(userId, List.of(locationDto));

    // Then
    verify(locationHistoryRepository, times(0)).save(any(VisitedLocation.class));
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
    AttractionDto attractionDto1 = new AttractionDto(attraction1.attractionId,"attraction1","","",0,0);
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(Arrays.asList(location1, location2));
    when(gpsUtil.getAttractions()).thenReturn(Collections.singletonList(attraction1));

    // When
    List<VisitedAttractionDto> visitedAttractions = gpsService.getVisitedAttractions(userId);

    // Then
    assertThat(visitedAttractions)
        .hasSize(1)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(new VisitedAttractionDto(attractionDto1, locationDto));
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
    List<VisitedAttractionDto> visitedAttractions = gpsService.getVisitedAttractions(userId);

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
    List<VisitedAttractionDto> visitedAttractions = gpsService.getVisitedAttractions(userId);

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
    AttractionDto expectedAttraction1 = new AttractionDto(attraction2.attractionId, "attraction2", "","",0,0 );
    AttractionDto expectedAttraction2 = new AttractionDto(attraction3.attractionId, "attraction3", "","",0,45);
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(List.of(location));
    when(gpsUtil.getAttractions()).thenReturn(Arrays.asList(attraction1, attraction2, attraction3));

    // When
    List<AttractionWithDistanceDto> attractionsWithDistance = gpsService.getNearbyAttractions(userId, limit);

    // Then
    assertThat(attractionsWithDistance)
        .hasSize(limit)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(
            new AttractionWithDistanceDto(expectedAttraction1, 0d),
            new AttractionWithDistanceDto(expectedAttraction2, 2700d * LocationServiceProperties.STATUTE_MILES_PER_NAUTICAL_MILE));
    verify(locationHistoryRepository, times(1)).findById(userId);
    verify(gpsUtil, times(1)).getAttractions();
  }

  @DisplayName("Get nearby attractions when no location registered should throw an exception")
  @Test
  void getNearbyAttractionsWhenEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    int limit = 2;
    when(locationHistoryRepository.findById(any(UUID.class))).thenReturn(Collections.emptyList());

    // Then
    assertThatThrownBy(() -> gpsService.getNearbyAttractions(userId,limit))
        .isInstanceOf(NoLocationFoundException.class)
        .hasMessageContaining("No location registered for the user yet");
    verify(locationHistoryRepository, times(1)).findById(userId);
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
    verify(locationHistoryRepository, times(0)).findById(any(UUID.class));
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
        .isEqualTo(distance * LocationServiceProperties.STATUTE_MILES_PER_NAUTICAL_MILE);
  }

}
