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
import java.util.Arrays;
import java.util.List;
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
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.UserRepository;
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
    List<Provider> providers = tourGuide.testutils.ProviderTestFactory.getProviders(user.getUserId());
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

}