package tourGuide.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.domain.User;
import tourGuide.domain.UserPreferences;
import tourGuide.domain.UserReward;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionsListDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.exception.NoLocationFoundException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.UserRepository;
import tourGuide.utils.EntitiesTestFactory;
import tripPricer.Provider;

@SpringBootTest
@ActiveProfiles("test")
class TourGuideServiceTest {

  @Autowired
  private TourGuideService tourGuideService;

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private TripDealsService tripDealsService;
  @MockBean
  private GpsService gpsService;
  @MockBean
  private RewardsService rewardsService;

  @DisplayName("Get user location should return last location dto")
  @Test
  void getUserLocationTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    LocationDto location = new LocationDto(45,-45);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(gpsService.getLastLocation(any(UUID.class)))
        .thenReturn(new VisitedLocationDto(user.getUserId(), location, new Date()));

    // When
    LocationDto actualDto = tourGuideService.getUserLocation("jon");

    //Then
    assertThat(actualDto).isEqualToComparingFieldByField(location);
    verify(userRepository, times(1)).findByUsername("jon");
    verify(gpsService, times(1)).getLastLocation(user.getUserId());
  }

  @DisplayName("Get user location when no location registered should return last tracked location dto")
  @Test
  void getUserLocationWhenNoLocationTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    LocationDto location = new LocationDto(45,-45);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(gpsService.getLastLocation(any(UUID.class))).thenThrow(new NoLocationFoundException("No user locations found"));
    when(gpsService.trackUserLocation(any(UUID.class))).thenReturn(new VisitedLocationDto(user.getUserId(), location, new Date()));

    // When
    LocationDto actualDto = tourGuideService.getUserLocation("jon");

    //Then
    assertThat(actualDto).isEqualToComparingFieldByField(location);
    verify(userRepository, times(1)).findByUsername("jon");
    verify(gpsService, times(1)).getLastLocation(user.getUserId());
    verify(gpsService, times(1)).trackUserLocation(user.getUserId());
  }

  @DisplayName("Get user location of non found user should throw an exception")
  @Test
  void getUserLocationNotFoundTest() {
    // Given
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.getUserLocation("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
  }

  @DisplayName("Get all users current locations should return a map of userId and location")
  @Test
  void getAllCurrentLocationsTest() throws Exception {
    // Given
    Map<UUID, LocationDto> expectedMap = new HashMap<>();

    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocationDto user1Location = new VisitedLocationDto(user.getUserId(), new LocationDto(0,0), new Date());
    expectedMap.put(user.getUserId(),user1Location.getLocation());

    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
    VisitedLocationDto user2Location = new VisitedLocationDto(user2.getUserId(), new LocationDto(45,45), new Date());
    expectedMap.put(user2.getUserId(), user2Location.getLocation());

    when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));
    when(gpsService.getLastLocation(any(UUID.class))).thenReturn(user1Location).thenReturn(user2Location);

    // When
    Map<UUID, LocationDto> actualMap = tourGuideService.getAllCurrentLocations();

    // Then
    assertThat(actualMap.entrySet()).usingRecursiveFieldByFieldElementComparator().isEqualTo(expectedMap.entrySet());
    verify(userRepository, times(1)).findAll();
    verify(gpsService, times(2)).getLastLocation(any(UUID.class));
  }

  @DisplayName("Get all users current locations when no users available should return an empty map")
  @Test
  void getAllCurrentLocationsWhenEmptyTest() {
    // Given
    when(userRepository.findAll()).thenReturn(new ArrayList<>());

    // When
    Map<UUID, LocationDto> actualMap = tourGuideService.getAllCurrentLocations();

    // Then
    assertThat(actualMap).isEmpty();
    verify(userRepository, times(1)).findAll();
  }

  @DisplayName("Get user rewards should return list of reward dto")
  @Test
  void getUserRewardsTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    Attraction attraction = new Attraction("Test1", "city", "state",45,-45);
    VisitedLocation visitedLocation = new VisitedLocation(user.getUserId(), new Location(45,-45), new Date());
    user.addUserReward(new UserReward(visitedLocation, attraction, 10));
    AttractionDto attractionDto = new AttractionDto(attraction.attractionId, -45,45, "Test1", "city", "state");
    VisitedLocationDto visitedLocationDto = new VisitedLocationDto(user.getUserId(), new LocationDto(-45,45), visitedLocation.timeVisited);
    UserRewardDto expectedDto = new UserRewardDto(visitedLocationDto, attractionDto, 10);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // When
    List<UserRewardDto> actualDto = tourGuideService.getUserRewards("jon");

    //Then
    assertThat(actualDto).usingRecursiveFieldByFieldElementComparator().containsExactly(expectedDto);
    verify(userRepository, times(1)).findByUsername("jon");
  }

  @DisplayName("Get user rewards of non found user should throw an exception")
  @Test
  void getUserRewardsNotFoundTest() {
    // Given
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.getUserRewards("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
  }

  @DisplayName("Get all users should return a list of all users")
  @Test
  void getAllUserTest() {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");
    when(userRepository.findAll()).thenReturn(Arrays.asList(user, user2));

    // When
    List<User> actualUsers = tourGuideService.getAllUsers();

    // Then
    assertThat(actualUsers).containsExactlyInAnyOrder(user, user2);
    verify(userRepository, times(1)).findAll();
  }

  @DisplayName("Get a user by username should return the corresponding user")
  @Test
  void getUserTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // When
    User actualUser = tourGuideService.getUser("jon");

    // Then
    assertThat(actualUser).isEqualTo(user);
    verify(userRepository, times(1)).findByUsername("jon");
  }

  @DisplayName("Get a user by a non existent username should throw an exception")
  @Test
  void getUserNotFoundTest() {
    // Given
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.getUser("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
  }

  @DisplayName("Get a user trip deals should return a list of provider")
  @Test
  void getTripDealsTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    user.setUserPreferences(new UserPreferences(0, Integer.MAX_VALUE, 1, 1,2,3));
    user.addUserReward(new UserReward(null, new Attraction("Test1", "", "",0,0), 10));
    List<Provider> providers = EntitiesTestFactory.getProviders(user.getUserId());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(tripDealsService.getTripDeals(any(UUID.class), any(UserPreferences.class), anyInt()))
        .thenReturn(providers);

    // When
    List<ProviderDto> actualDtos = tourGuideService.getTripDeals("jon");

    //Then
    assertThat(actualDtos).usingFieldByFieldElementComparator().isEqualTo(providers);
    assertThat(user.getTripDeals()).isEqualTo(providers);
    verify(userRepository, times(1)).findByUsername("jon");
    verify(tripDealsService, times(1)).getTripDeals(user.getUserId(), user.getUserPreferences(), 10);
  }

  @DisplayName("Get a non existent user trip deals should throw an exception")
  @Test
  void getTripDealsNotFoundTest() {
    // Given
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.getTripDeals("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
    verify(tripDealsService, times(0)).getTripDeals(any(UUID.class),any(UserPreferences.class), anyInt());
  }

  @DisplayName("Get user preferences should return a user preferences dto")
  @Test
  void getUserPreferenceTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    user.setUserPreferences(new UserPreferences(0,Integer.MAX_VALUE,1,1,2,3));
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0,Integer.MAX_VALUE,1, 1,2,3);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // When
    UserPreferencesDto actualDto = tourGuideService.getUserPreferences("jon");

    //Then
    assertThat(actualDto).isEqualToComparingFieldByFieldRecursively(userPreferencesDto);
    verify(userRepository, times(1)).findByUsername("jon");
  }

  @DisplayName("Get user preferences of non found user should throw an exception")
  @Test
  void getUserPreferencesNotFoundTest() {
    // Given
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.getUserPreferences("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
  }

  @DisplayName("Set user preferences should update user preferences")
  @Test
  void setUserPreferenceTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0, Integer.MAX_VALUE,1, 1,2,3);
    UserPreferences userPreferences = new UserPreferences(0, Integer.MAX_VALUE,1,1,2,3);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // When
    UserPreferencesDto actualDto = tourGuideService.setUserPreferences("jon", userPreferencesDto);

    //Then
    assertThat(actualDto).isEqualTo(userPreferencesDto);
    assertThat(user.getUserPreferences()).isEqualToComparingFieldByFieldRecursively(userPreferences);
    verify(userRepository, times(1)).findByUsername("jon");
  }

  @DisplayName("Set user preferences of non found user should throw an exception")
  @Test
  void setUserPreferencesNotFoundTest() {
    // Given
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0, Integer.MAX_VALUE,1, 1,2,3);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.setUserPreferences("nonExistent", userPreferencesDto))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
  }

  @DisplayName("Set nearby attractions should should return dto with user location and 5 attractions")
  @Test
  void getNearByAttractionsTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocation userLocation = new VisitedLocation(user.getUserId(), new Location(0,0), new Date());
    user.addToVisitedLocations(userLocation);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(gpsService.getTopNearbyAttractionsWithDistances(any(Location.class),anyInt()))
        .thenReturn(EntitiesTestFactory.getAttractionsWithDistance());
    when(rewardsService.getRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(100);


    // When
    NearbyAttractionsListDto actualDto = tourGuideService.getNearByAttractions("jon");

    //Then
    assertThat(actualDto.getUserLocation()).isEqualToComparingFieldByField(userLocation.location);
    assertThat(actualDto.getAttractions())
        .hasSize(5)
        .usingFieldByFieldElementComparator().containsOnlyElementsOf(EntitiesTestFactory.getAttractionsDto());
    verify(userRepository, times(1)).findByUsername("jon");
    verify(gpsService, times(1)).getTopNearbyAttractionsWithDistances(userLocation.location,5);
    verify(rewardsService, times(5)).getRewardPoints(any(UUID.class), any(UUID.class));
  }

  @DisplayName("Set nearby attractions of non found user should throw an exception")
  @Test
  void getNearByAttractionsNotFoundTest() {
    // Given
    UserPreferencesDto userPreferencesDto = new UserPreferencesDto(0, Integer.MAX_VALUE,1, 1,2,3);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.getNearByAttractions("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
  }

  @DisplayName("Track user location should return current visited location Dto")
  @Test
  void trackUserLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(0,0), new Date());
    when(gpsService.trackUserLocation(any(UUID.class))).thenReturn(visitedLocation);

    // When
    VisitedLocationDto actualDto = tourGuideService.trackUserLocation(userId);

    //Then
    assertThat(actualDto).isEqualToComparingFieldByFieldRecursively(visitedLocation);
    verify(gpsService, times(1)).trackUserLocation(userId);
  }

  @DisplayName("calculate user rewards should retrieve visited location and calculate rewards")
  @Test
  void calculateRewardsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(0,0), new Date());
    AttractionDto attraction = new AttractionDto(UUID.randomUUID(),0,0,"attraction", "", "");
    Map<AttractionDto, VisitedLocationDto> attractionToReward = new HashMap<>();
    attractionToReward.put(attraction, visitedLocation);
    when(gpsService.getVisitedAttractions(any(UUID.class))).thenReturn(attractionToReward);

    // When
    tourGuideService.calculateRewards(userId);

    //Then
    verify(gpsService, times(1)).getVisitedAttractions(userId);
    verify(rewardsService, times(1)).calculateRewards(userId, attractionToReward);
  }

}
