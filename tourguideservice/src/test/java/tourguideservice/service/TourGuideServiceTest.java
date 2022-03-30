package tourguideservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import tourguideservice.domain.User;
import tourguideservice.domain.UserPreferences;
import tourguideservice.dto.AttractionDto;
import tourguideservice.dto.LocationDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import tourguideservice.dto.UserRewardDto;
import tourguideservice.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;
import tourguideservice.exception.UserNotFoundException;
import tourguideservice.repository.UserRepository;
import tourguideservice.service.proxy.TripServiceProxy;
import tourguideservice.utils.EntitiesTestFactory;
import tripPricer.Provider;

@SpringBootTest
@ActiveProfiles("test")
class TourGuideServiceTest {

  @Autowired
  private TourGuideService tourGuideService;

  @MockBean
  private UserRepository userRepository;
  @MockBean
  private TripServiceProxy tripDealsService;
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
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(location);
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
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(location);
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
    AttractionDto attractionDto = new AttractionDto(UUID.randomUUID(), -45,45, "Test1", "city", "state");
    VisitedLocationDto visitedLocationDto = new VisitedLocationDto(user.getUserId(), new LocationDto(-45,45), new Date());
    UserRewardDto reward = new UserRewardDto(visitedLocationDto, attractionDto, 10);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(rewardsService.getAllRewards(any(UUID.class))).thenReturn(Collections.singletonList(reward));

    // When
    List<UserRewardDto> actualDto = tourGuideService.getUserRewards("jon");

    //Then
    assertThat(actualDto).usingRecursiveFieldByFieldElementComparator().containsExactly(reward);
    verify(userRepository, times(1)).findByUsername("jon");
    verify(rewardsService, times(1)).getAllRewards(user.getUserId());
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
    verify(rewardsService, times(0)).getAllRewards(any(UUID.class));
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
    UUID attractionId = UUID.randomUUID();
    Map<AttractionDto, Double> nearbyAttractions = new HashMap<>();
    nearbyAttractions.put(new AttractionDto(attractionId,0,0,"attraction","",""), 0d);
    user.setUserPreferences(new UserPreferences(0, Integer.MAX_VALUE, 1, 1,2,3));
    List<ProviderDto> providers = EntitiesTestFactory.getProvidersDto(user.getUserId());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(gpsService.getNearbyAttractions(any(UUID.class), anyInt())).thenReturn(nearbyAttractions);
    when(rewardsService.getTotalRewardPoints(any(UUID.class))).thenReturn(10);
    when(tripDealsService.getTripDeals(any(UUID.class), any(shared.dto.PreferencesDto.class), anyInt()))
        .thenReturn(providers);

    // When
    List<ProviderDto> actualDtos = tourGuideService.getTripDeals("jon");

    //Then
    List<Provider> expectedProviders = EntitiesTestFactory.getProviders(user.getUserId());
    assertThat(actualDtos).usingRecursiveComparison().isEqualTo(providers);
    assertThat(user.getTripDeals()).usingRecursiveComparison().isEqualTo(expectedProviders);
    verify(userRepository, times(1)).findByUsername("jon");
    verify(gpsService, times(1)).getNearbyAttractions(user.getUserId(), 1);
    verify(rewardsService, times(1)).getTotalRewardPoints(user.getUserId());
    verify(tripDealsService, times(1)).getTripDeals(any(UUID.class), any(PreferencesDto.class), anyInt());
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
    verify(tripDealsService, times(0)).getTripDeals(any(UUID.class),any(shared.dto.PreferencesDto.class), anyInt());
  }

  @DisplayName("Get user preferences should return a user preferences dto")
  @Test
  void getUserPreferenceTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    user.setUserPreferences(new UserPreferences(0,Integer.MAX_VALUE,1,1,2,3));
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE),1, 1,2,3);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // When
    PreferencesDto actualDto = tourGuideService.getUserPreferences("jon");

    //Then
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(preferencesDto);
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
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE),1, 1,2,3);
    UserPreferences userPreferences = new UserPreferences(0, Integer.MAX_VALUE,1,1,2,3);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

    // When
    PreferencesDto actualDto = tourGuideService.setUserPreferences("jon", preferencesDto);

    //Then
    assertThat(actualDto).isEqualTo(preferencesDto);
    assertThat(user.getUserPreferences()).usingRecursiveComparison().isEqualTo(userPreferences);
    verify(userRepository, times(1)).findByUsername("jon");
  }

  @DisplayName("Set user preferences of non found user should throw an exception")
  @Test
  void setUserPreferencesNotFoundTest() {
    // Given
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE),1, 1,2,3);
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

    // Then
    assertThatThrownBy(() -> tourGuideService.setUserPreferences("nonExistent", preferencesDto))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userRepository, times(1)).findByUsername("nonExistent");
  }

  @DisplayName("Set nearby attractions should should return dto with user location and 5 attractions")
  @Test
  void getNearByAttractionsTest() throws Exception {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocationDto userLocation = new VisitedLocationDto(user.getUserId(), new LocationDto(0,0), new Date());
    when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
    when(gpsService.getLastLocation(any(UUID.class))).thenReturn(userLocation);
    when(gpsService.getNearbyAttractions(any(UUID.class),anyInt()))
        .thenReturn(EntitiesTestFactory.getAttractionsWithDistance());
    when(rewardsService.getRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(100);


    // When
    NearbyAttractionsListDto actualDto = tourGuideService.getNearByAttractions("jon");

    //Then
    assertThat(actualDto.getUserLocation()).usingRecursiveComparison().isEqualTo(userLocation.getLocation());
    assertThat(actualDto.getAttractions())
        .hasSize(5)
        .usingRecursiveFieldByFieldElementComparator().hasSameElementsAs(EntitiesTestFactory.getAttractionsDto());
    verify(userRepository, times(1)).findByUsername("jon");
    verify(gpsService, times(1)).getLastLocation(user.getUserId());
    verify(gpsService, times(1)).getNearbyAttractions(user.getUserId(),5);
    verify(rewardsService, times(5)).getRewardPoints(any(UUID.class), any(UUID.class));
  }

  @DisplayName("Set nearby attractions of non found user should throw an exception")
  @Test
  void getNearByAttractionsNotFoundTest() {
    // Given
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE),1, 1,2,3);
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
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(visitedLocation);
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
