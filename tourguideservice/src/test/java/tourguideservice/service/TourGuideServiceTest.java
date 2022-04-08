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
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import shared.dto.UserDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.AttractionDto;
import shared.dto.LocationDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import shared.dto.UserRewardDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;
import shared.exception.UserNotFoundException;
import tourguideservice.proxy.LocationServiceProxy;
import tourguideservice.proxy.RewardServiceProxy;
import tourguideservice.proxy.TripServiceProxy;
import tourguideservice.proxy.UserServiceProxy;
import tourguideservice.utils.EntitiesTestFactory;

@SpringBootTest(properties = {"tourguide.test.trackingOnStart=false",
    "tourguide.test.useInternalUser=false"})
@ActiveProfiles("test")
class TourGuideServiceTest {

  @Autowired
  private TourGuideService tourGuideService;

  @MockBean
  private UserServiceProxy userServiceProxy;
  @MockBean
  private TripServiceProxy tripServiceProxy;
  @MockBean
  private LocationServiceProxy locationServiceProxy;
  @MockBean
  private RewardServiceProxy rewardServiceProxy;

  @DisplayName("Get user location should return last location dto")
  @Test
  void getUserLocationTest() throws Exception {
    // Given
    UserDto user = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    LocationDto location = new LocationDto(45, -45);
    when(userServiceProxy.getUser(anyString())).thenReturn(user);
    when(locationServiceProxy.getLastVisitedLocation(any(UUID.class)))
        .thenReturn(new VisitedLocationDto(user.getUserId(), location, new Date()));

    // When
    LocationDto actualDto = tourGuideService.getUserLocation("jon");

    //Then
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(location);
    verify(userServiceProxy, times(1)).getUser("jon");
    verify(locationServiceProxy, times(1)).getLastVisitedLocation(user.getUserId());
  }

  @DisplayName("Get user location when no location registered should return last tracked location dto")
  @Test
  void getUserLocationWhenNoLocationTest() throws Exception {
    // Given
    UserDto user = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    LocationDto location = new LocationDto(45, -45);
    when(userServiceProxy.getUser(anyString())).thenReturn(user);
    when(locationServiceProxy.getLastVisitedLocation(any(UUID.class))).thenThrow(
        new NoLocationFoundException("No user locations found"));
    when(locationServiceProxy.trackUserLocation(any(UUID.class))).thenReturn(
        new VisitedLocationDto(user.getUserId(), location, new Date()));

    // When
    LocationDto actualDto = tourGuideService.getUserLocation("jon");

    //Then
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(location);
    verify(userServiceProxy, times(1)).getUser("jon");
    verify(locationServiceProxy, times(1)).getLastVisitedLocation(user.getUserId());
    verify(locationServiceProxy, times(1)).trackUserLocation(user.getUserId());
  }

  @DisplayName("Get user location of non found user should throw an exception")
  @Test
  void getUserLocationNotFoundTest() throws Exception {
    // Given
    when(userServiceProxy.getUser(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> tourGuideService.getUserLocation("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userServiceProxy, times(1)).getUser("nonExistent");
  }

  @DisplayName("Get all users current locations should return a map of userId and location")
  @Test
  void getAllCurrentLocationsTest() {
    // Given
    Map<UUID, LocationDto> expectedMap = new HashMap<>();

    UUID user1Id = UUID.randomUUID();
    VisitedLocationDto user1Location = new VisitedLocationDto(user1Id, new LocationDto(0, 0), new Date());
    expectedMap.put(user1Id, user1Location.getLocation());

    UUID user2Id = UUID.randomUUID();
    VisitedLocationDto user2Location = new VisitedLocationDto(user2Id, new LocationDto(45, 45), new Date());
    expectedMap.put(user2Id, user2Location.getLocation());

    when(locationServiceProxy.getAllUserLastVisitedLocation()).thenReturn(List.of(user1Location, user2Location));

    // When
    Map<UUID, LocationDto> actualMap = tourGuideService.getAllCurrentLocations();

    // Then
    assertThat(actualMap.entrySet()).usingRecursiveFieldByFieldElementComparator()
        .isEqualTo(expectedMap.entrySet());
    verify(locationServiceProxy, times(1)).getAllUserLastVisitedLocation();
  }

  @DisplayName("Get all users current locations when no users available should return an empty map")
  @Test
  void getAllCurrentLocationsWhenEmptyTest() {
    // Given
    when(locationServiceProxy.getAllUserLastVisitedLocation()).thenReturn(new ArrayList<>());

    // When
    Map<UUID, LocationDto> actualMap = tourGuideService.getAllCurrentLocations();

    // Then
    assertThat(actualMap).isEmpty();
    verify(locationServiceProxy, times(1)).getAllUserLastVisitedLocation();
  }

  @DisplayName("Get user rewards should return list of reward dto")
  @Test
  void getUserRewardsTest() throws Exception {
    // Given
    UserDto user = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    AttractionDto attractionDto = new AttractionDto(UUID.randomUUID(), "Test1", "city", "state", 45, -45);
    VisitedLocationDto visitedLocationDto = new VisitedLocationDto(user.getUserId(), new LocationDto(-45, 45), new Date());
    UserRewardDto reward = new UserRewardDto(visitedLocationDto, attractionDto, 10);
    when(userServiceProxy.getUser(anyString())).thenReturn(user);
    when(rewardServiceProxy.getAllRewards(any(UUID.class))).thenReturn(Collections.singletonList(reward));

    // When
    List<UserRewardDto> actualDto = tourGuideService.getUserRewards("jon");

    //Then
    assertThat(actualDto).usingRecursiveFieldByFieldElementComparator().containsExactly(reward);
    verify(userServiceProxy, times(1)).getUser("jon");
    verify(rewardServiceProxy, times(1)).getAllRewards(user.getUserId());
  }

  @DisplayName("Get user rewards of non found user should throw an exception")
  @Test
  void getUserRewardsNotFoundTest() throws Exception {
    // Given
    when(userServiceProxy.getUser(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> tourGuideService.getUserRewards("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userServiceProxy, times(1)).getUser("nonExistent");
    verify(rewardServiceProxy, times(0)).getAllRewards(any(UUID.class));
  }


  @DisplayName("Get a user trip deals should return a list of provider")
  @Test
  void getTripDealsTest() throws Exception {
    // Given
    UserDto user = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.TEN,4,3,2,1);
    AttractionDto attractionDto = new AttractionDto(UUID.randomUUID(), "attraction", "", "", 0, 0);
    List<AttractionWithDistanceDto> nearbyAttractions =  List.of(new AttractionWithDistanceDto(attractionDto,0d));
    List<ProviderDto> providers = EntitiesTestFactory.getProvidersDto(user.getUserId());

    when(userServiceProxy.getUser(anyString())).thenReturn(user);
    when(userServiceProxy.getUserPreferences(anyString())).thenReturn(preferencesDto);
    when(locationServiceProxy.getNearbyAttractions(any(UUID.class), anyInt())).thenReturn(nearbyAttractions);
    when(rewardServiceProxy.getTotalRewardPoints(any(UUID.class))).thenReturn(10);
    when(tripServiceProxy.getTripDeals(any(UUID.class), any(shared.dto.PreferencesDto.class), anyInt()))
        .thenReturn(providers);

    // When
    List<ProviderDto> actualDtos = tourGuideService.getTripDeals("jon");

    //Then
    assertThat(actualDtos).usingRecursiveComparison().isEqualTo(providers);
    verify(userServiceProxy, times(1)).getUser("jon");
    verify(locationServiceProxy, times(1)).getNearbyAttractions(user.getUserId(), 1);
    verify(rewardServiceProxy, times(1)).getTotalRewardPoints(user.getUserId());
    verify(tripServiceProxy, times(1))
        .getTripDeals(attractionDto.getAttractionId(), preferencesDto, 10);
  }

  @DisplayName("Get user trip deals when location can't be retrieve should throw an exception")
  @Test
  void getTripDealsWhenLocationCantBeRetrieveTest() throws Exception {
    // Given
    UserDto user = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

    when(userServiceProxy.getUser(anyString())).thenReturn(user);
    when(locationServiceProxy.getNearbyAttractions(any(UUID.class), anyInt()))
        .thenThrow(new NoLocationFoundException("No Location registered for this user yet"));

    // Then
    assertThatThrownBy(() -> tourGuideService.getTripDeals("jon"))
        .isInstanceOf(IllegalStateException.class);
    verify(userServiceProxy, times(1)).getUser("jon");
    verify(locationServiceProxy, times(2)).getNearbyAttractions(user.getUserId(),1);
  }

  @DisplayName("Get a non existent user trip deals should throw an exception")
  @Test
  void getTripDealsNotFoundTest() throws Exception {
    // Given
    when(userServiceProxy.getUser(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> tourGuideService.getTripDeals("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userServiceProxy, times(1)).getUser("nonExistent");
    verify(tripServiceProxy, times(0)).getTripDeals(any(UUID.class),
        any(shared.dto.PreferencesDto.class), anyInt());
  }

  @DisplayName("Get user preferences should return a user preferences dto")
  @Test
  void getUserPreferenceTest() throws Exception {
    // Given
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);
    when(userServiceProxy.getUserPreferences(anyString())).thenReturn(preferencesDto);

    // When
    PreferencesDto actualDto = tourGuideService.getUserPreferences("jon");

    //Then
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(preferencesDto);
    verify(userServiceProxy, times(1)).getUserPreferences("jon");
  }

  @DisplayName("Get user preferences of non found user should throw an exception")
  @Test
  void getUserPreferencesNotFoundTest() throws Exception {
    // Given
    when(userServiceProxy.getUserPreferences(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> tourGuideService.getUserPreferences("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userServiceProxy, times(1)).getUserPreferences("nonExistent");
  }

  @DisplayName("Set user preferences should update user preferences")
  @Test
  void setUserPreferenceTest() throws Exception {
    // Given
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);
    when(userServiceProxy.setUserPreferences(anyString(), any(PreferencesDto.class))).thenReturn(preferencesDto);

    // When
    PreferencesDto actualDto = tourGuideService.setUserPreferences("jon", preferencesDto);

    //Then
    assertThat(actualDto).isEqualTo(preferencesDto);
    verify(userServiceProxy, times(1)).setUserPreferences("jon", preferencesDto);
  }

  @DisplayName("Set user preferences of non found user should throw an exception")
  @Test
  void setUserPreferencesNotFoundTest() throws Exception {
    // Given
    PreferencesDto preferencesDto = new PreferencesDto(BigDecimal.ZERO, BigDecimal.valueOf(Integer.MAX_VALUE), 1, 1, 2, 3);
    when(userServiceProxy.setUserPreferences(anyString(), any(PreferencesDto.class))).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> tourGuideService.setUserPreferences("nonExistent", preferencesDto))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userServiceProxy, times(1)).setUserPreferences("nonExistent", preferencesDto);
  }

  @DisplayName("Get nearby attractions should should return dto with user location and 5 attractions")
  @Test
  void getNearByAttractionsTest() throws Exception {
    // Given
    UserDto user = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    VisitedLocationDto userLocation = new VisitedLocationDto(user.getUserId(), new LocationDto(0, 0), new Date());
    when(userServiceProxy.getUser(anyString())).thenReturn(user);
    when(locationServiceProxy.getLastVisitedLocation(any(UUID.class))).thenReturn(userLocation);
    when(locationServiceProxy.getNearbyAttractions(any(UUID.class), anyInt()))
        .thenReturn(EntitiesTestFactory.getAttractionsWithDistance());
    when(rewardServiceProxy.getRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(100);


    // When
    NearbyAttractionsListDto actualDto = tourGuideService.getNearByAttractions("jon");

    //Then
    assertThat(actualDto.getUserLocation()).usingRecursiveComparison()
        .isEqualTo(userLocation.getLocation());
    assertThat(actualDto.getAttractions())
        .hasSize(5)
        .usingRecursiveFieldByFieldElementComparator()
        .hasSameElementsAs(EntitiesTestFactory.getAttractionsDto());
    verify(userServiceProxy, times(1)).getUser("jon");
    verify(locationServiceProxy, times(1)).getLastVisitedLocation(user.getUserId());
    verify(locationServiceProxy, times(1)).getNearbyAttractions(user.getUserId(), 5);
    verify(rewardServiceProxy, times(5)).getRewardPoints(any(UUID.class), any(UUID.class));
  }

  @DisplayName("Get nearby attractions of non found user should throw an exception")
  @Test
  void getNearByAttractionsNotFoundTest() throws Exception {
    // Given
    when(userServiceProxy.getUser(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> tourGuideService.getNearByAttractions("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userServiceProxy, times(1)).getUser("nonExistent");
  }

  @DisplayName("Track user location should return current visited location Dto")
  @Test
  void trackUserLocationTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(0, 0), new Date());
    when(locationServiceProxy.trackUserLocation(any(UUID.class))).thenReturn(visitedLocation);

    // When
    VisitedLocationDto actualDto = tourGuideService.trackUserLocation(userId);

    //Then
    assertThat(actualDto).usingRecursiveComparison().isEqualTo(visitedLocation);
    verify(locationServiceProxy, times(1)).trackUserLocation(userId);
  }

  @DisplayName("calculate user rewards should retrieve visited location and calculate rewards")
  @Test
  void calculateRewardsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    VisitedLocationDto visitedLocation = new VisitedLocationDto(userId, new LocationDto(0, 0), new Date());
    AttractionDto attraction = new AttractionDto(UUID.randomUUID(), "attraction", "", "", 0, 0);
    List<VisitedAttractionDto> attractionToReward = List.of(new VisitedAttractionDto(attraction, visitedLocation));
    when(locationServiceProxy.getVisitedAttractions(any(UUID.class))).thenReturn(attractionToReward);

    // When
    tourGuideService.calculateRewards(userId);

    //Then
    verify(locationServiceProxy, times(1)).getVisitedAttractions(userId);
    verify(rewardServiceProxy, times(1)).calculateRewards(userId, attractionToReward);
  }

  @DisplayName("Get all users should return a list of all users")
  @Test
  void getAllUserTest() {
    // Given
    UUID user1Id = UUID.randomUUID();
    UUID user2Id = UUID.randomUUID();
    when(userServiceProxy.getAllUserId()).thenReturn(Arrays.asList(user1Id, user2Id));

    // When
    List<UUID> actualUsersId = tourGuideService.getAllUsersId();

    // Then
    assertThat(actualUsersId).containsExactlyInAnyOrder(user1Id, user2Id);
    verify(userServiceProxy, times(1)).getAllUserId();
  }

  @DisplayName("Get a user by username should return the corresponding user")
  @Test
  void getUserTest() throws Exception {
    // Given
    UserDto user = new UserDto(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    when(userServiceProxy.getUser(anyString())).thenReturn(user);

    // When
    UUID actualUserId = tourGuideService.getUserId("jon");

    // Then
    assertThat(actualUserId).isEqualTo(user.getUserId());
    verify(userServiceProxy, times(1)).getUser("jon");
  }

  @DisplayName("Get a user by a non existent username should throw an exception")
  @Test
  void getUserNotFoundTest() throws Exception {
    // Given
    when(userServiceProxy.getUser(anyString())).thenThrow(new UserNotFoundException("User not found"));

    // Then
    assertThatThrownBy(() -> tourGuideService.getUserId("nonExistent"))
        .isInstanceOf(UserNotFoundException.class)
        .hasMessageContaining("User not found");
    verify(userServiceProxy, times(1)).getUser("nonExistent");
  }

}
