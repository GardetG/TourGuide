package tourguideservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
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
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import rewardCentral.RewardCentral;
import tourguideservice.domain.UserReward;
import shared.dto.AttractionDto;
import shared.dto.LocationDto;
import tourguideservice.dto.UserRewardDto;
import shared.dto.VisitedLocationDto;
import tourguideservice.repository.RewardsRepository;

@SpringBootTest
@ActiveProfiles("test")
class RewardServiceTest {

  @Autowired
  private RewardsService rewardsService;

  @MockBean
  private RewardCentral rewardCentral;
  @MockBean
  private RewardsRepository rewardsRepository;

  @Captor
  ArgumentCaptor<UserReward> userRewardCaptor;

  @DisplayName("Get all user rewards should return list of rewards Dto")
  @Test
  void getAllRewardsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Date date = new Date();
    UserReward reward = new UserReward(
        new VisitedLocation(userId, new Location(45,-45), date),
        new Attraction("Test1", "city", "state",45,-45),
        10);
    UserRewardDto expectedDto = new UserRewardDto(
        new VisitedLocationDto(userId, new LocationDto(45,-45), date),
        new AttractionDto(reward.attraction.attractionId, "Test1", "city", "state",45,-45),
        10);
    when(rewardsRepository.findById(any(UUID.class))).thenReturn(Collections.singletonList(reward));

    // When
    List<UserRewardDto> actualDto = rewardsService.getAllRewards(userId);

    //Then
    assertThat(actualDto).usingRecursiveFieldByFieldElementComparator().containsExactly(expectedDto);
    verify(rewardsRepository, times(1)).findById(userId);
  }

  @DisplayName("Get all user rewards when no rewards registered should return an empty list")
  @Test
  void getAllRewardsEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    when(rewardsRepository.findById(any(UUID.class))).thenReturn(new ArrayList<>());

    // When
    List<UserRewardDto> actualDto = rewardsService.getAllRewards(userId);

    //Then
    assertThat(actualDto).isEmpty();
    verify(rewardsRepository, times(1)).findById(userId);
  }

  @DisplayName("Get total rewards points should return sum of all rewards point")
  @Test
  void getTotalRewardPointsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Attraction attraction1 = new Attraction("Test1", "city", "state",45,-45);
    Attraction attraction2 = new Attraction("Test2", "city", "state",45,-45);
    VisitedLocation visitedLocation = new VisitedLocation(userId, new Location(45,-45), new Date());
    UserReward reward1 = new UserReward(visitedLocation, attraction1, 7);
    UserReward reward2 = new UserReward(visitedLocation, attraction2, 3);
    when(rewardsRepository.findById(any(UUID.class))).thenReturn(Arrays.asList(reward1, reward2));

    // When
    int total = rewardsService.getTotalRewardPoints(userId);

    //Then
    assertThat(total).isEqualTo(10);
    verify(rewardsRepository, times(1)).findById(userId);
  }

  @DisplayName("Get total rewards points when no rewards registered should return 0")
  @Test
  void getTotalRewardPointsWhenEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    when(rewardsRepository.findById(any(UUID.class))).thenReturn(new ArrayList<>());

    // When
    int total = rewardsService.getTotalRewardPoints(userId);

    //Then
    assertThat(total).isZero();
    verify(rewardsRepository, times(1)).findById(userId);
  }

  @DisplayName("Get total rewards points when no rewards registered should return 0")
  @Test
  void calculateRewardsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    UUID attractionId = UUID.randomUUID();
    Date date = new Date();
    AttractionDto attraction = new AttractionDto(attractionId, "attraction", "", "",0,0);
    VisitedLocationDto location = new VisitedLocationDto(userId, new LocationDto(0,0), date);
    Map<AttractionDto, VisitedLocationDto> attractionToReward = new HashMap<>();
    attractionToReward.put(attraction, location);
    when(rewardCentral.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(10);

    // When
    rewardsService.calculateRewards(userId, attractionToReward);

    //Then
    verify(rewardsRepository, times(1)).save(userRewardCaptor.capture());
    assertThat(userRewardCaptor.getValue().visitedLocation)
        .usingRecursiveComparison().isEqualTo(new VisitedLocation(userId, new Location(0,0), date));
    assertThat(userRewardCaptor.getValue().attraction.attractionName).isEqualTo("attraction");
    assertThat(userRewardCaptor.getValue().getRewardPoints()).isEqualTo(10);
  }

  @DisplayName("Calculate reward when no attraction to reward should not registered any reward")
  @Test
  void calculateRewardsWhenEmptyTest() {
    // Given
    UUID userId = UUID.randomUUID();
    Map<AttractionDto, VisitedLocationDto> attractionToReward = new HashMap<>();

    // When
    rewardsService.calculateRewards(userId, attractionToReward);

    //Then
    verify(rewardsRepository, times(0)).save(any(UserReward.class));
  }

  @DisplayName("Get reward points should return points for the corresponding user and attraction")
  @Test
  void getRewardPointsTest() {
    // Given
    UUID userId = UUID.randomUUID();
    UUID attractionId = UUID.randomUUID();
    when(rewardCentral.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(10);

    // When
    int rewardPoints = rewardsService.getRewardPoints(attractionId, userId);

    // Then
    assertThat(rewardPoints).isEqualTo(10);
    verify(rewardCentral, times(1)).getAttractionRewardPoints(attractionId, userId);
  }

}
