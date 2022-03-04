package tourGuide.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import java.util.Date;
import java.util.Locale;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import tourGuide.domain.User;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.Tracker;

@SpringBootTest(properties = "tourguide.internaluser.internalUserNumber=1")
@Profile({"test", "internalUser"})
class TestRewardsService {

  @Autowired
  private RewardsService rewardsService;
  @Autowired
  private TourGuideService tourGuideService;
  @Autowired
  private Tracker tracker;
  @Autowired
  private GpsUtil gpsUtil;

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
  void userGetRewards() throws Exception {
    // Given
    String userName = "internalUser0";
    User user = tourGuideService.getUser(userName);
    Attraction attraction = gpsUtil.getAttractions().get(0);
    user.addToVisitedLocations(new VisitedLocation(user.getUserId(), attraction, new Date()));

    // When
    rewardsService.calculateRewards(user);

    // THen
    assertThat(user.getUserRewards()).hasSize(1);
  }

  @Test
  void isWithinAttractionProximity() {
    // Given
    Attraction attraction = gpsUtil.getAttractions().get(0);

    // Then
    assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
  }

  // Needs fixed - can throw ConcurrentModificationException
  @Test
  void nearAllAttractions() throws Exception {
    // Given
    String userName = "internalUser0";
    User user = tourGuideService.getUser(userName);
    rewardsService.setProximityBuffer(Integer.MAX_VALUE);

    // When
    rewardsService.calculateRewards(user);
    assertThat(user.getUserRewards()).hasSameSizeAs(gpsUtil.getAttractions());
  }

}
