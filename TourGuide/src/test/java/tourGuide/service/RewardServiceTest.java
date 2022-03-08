package tourGuide.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Date;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import rewardCentral.RewardCentral;
import tourGuide.domain.User;

@SpringBootTest
@ActiveProfiles("test")
class RewardServiceTest {

  @Autowired
  private RewardsService rewardsService;

  @MockBean
  private RewardCentral rewardCentral;

  @DisplayName("Get user location should return user location from GpsUtil library")
  @Test
  void getUserLocationTest() {
    // Given
    User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
    Attraction attraction = new Attraction("attraction", "", "", 0,0);
    when(rewardCentral.getAttractionRewardPoints(any(UUID.class), any(UUID.class))).thenReturn(10);

    // When
    int rewardPoints = rewardsService.getRewardPoints(attraction, user);

    // Then
    assertThat(rewardPoints).isEqualTo(10);
    verify(rewardCentral, times(1)).getAttractionRewardPoints(attraction.attractionId, user.getUserId());
  }

}
