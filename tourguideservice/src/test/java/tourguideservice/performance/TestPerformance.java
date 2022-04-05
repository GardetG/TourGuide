package tourguideservice.performance;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourguideservice.domain.User;
import shared.dto.AttractionDto;
import shared.dto.LocationDto;
import shared.dto.VisitedLocationDto;
import tourguideservice.service.TourGuideService;
import tourguideservice.service.proxy.LocationServiceProxy;
import tourguideservice.service.proxy.RewardServiceProxy;

@Tag("performance")
@SpringBootTest(properties = {"tourguide.test.trackingOnStart=false",
    "tourguide.test.internalUserNumber=10"})
@ActiveProfiles({"test"})
class TestPerformance {

  /*
   * A note on performance improvements:
   *
   *     The number of users generated for the high volume tests can be easily adjusted via this method:
   *
   *     		InternalTestHelper.setInternalUserNumber(100000);
   *
   *
   *     These tests can be modified to suit new solutions, just as long as the performance metrics
   *     at the end of the tests remains consistent.
   *
   *     These are performance metrics that we are trying to hit:
   *
   *     highVolumeTrackLocation: 100,000 users within 15 minutes:
   *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   *
   *     highVolumeGetRewards: 100,000 users within 20 minutes:
   *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
   */

  @Autowired
  private TourGuideService tourGuideService;
  @Autowired
  private LocationServiceProxy locationServiceProxy;
  @Autowired
  private RewardServiceProxy rewardServiceProxy;

  @BeforeAll
  public static void setDefaultLocale() {
    Locale.setDefault(Locale.UK);
  }

  @Test
  void highVolumeTrackLocation() {
    // Given
    List<User> allUsers = tourGuideService.getAllUsers();

    // When
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    ExecutorService executor = Executors.newFixedThreadPool(200);
    List<CompletableFuture<?>> result = allUsers.stream()
        .map(user -> CompletableFuture.runAsync(
            () -> tourGuideService.trackUserLocation(user.getUserId()), executor))
        .collect(Collectors.toList());
    result.forEach(CompletableFuture::join);
    stopWatch.stop();

    // Then
    System.out.println("highVolumeTrackLocation: Time Elapsed: " +
        TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
    assertTrue(
        TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

  @Test
  void highVolumeGetRewards() {
    // Given
    ExecutorService testExecutor = Executors.newFixedThreadPool(200);
    List<User> allUsers = tourGuideService.getAllUsers();
    AttractionDto attraction = locationServiceProxy.getAttractions().get(0);
    LocationDto attractionLocation = new LocationDto(attraction.getLatitude(), attraction.getLongitude());

    // Add a location near the first attraction
    List<CompletableFuture<Void>> usersInit = allUsers.stream()
        .map(user -> CompletableFuture.runAsync(() -> locationServiceProxy.addVisitedLocation(
            List.of(new VisitedLocationDto(user.getUserId(), attractionLocation, new Date())),
            user.getUserId()
        ), testExecutor))
        .collect(Collectors.toList());
    usersInit.forEach(CompletableFuture::join);

    // When
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    ExecutorService executor = Executors.newFixedThreadPool(200);
    List<CompletableFuture<?>> result = allUsers.stream()
        .map(user -> CompletableFuture.runAsync(
            () -> tourGuideService.calculateRewards(user.getUserId()), executor))
        .collect(Collectors.toList());
    result.forEach(CompletableFuture::join);

    stopWatch.stop();

    // Then
    List<CompletableFuture<Integer>> rewardListSize = allUsers.stream()
        .map(user -> CompletableFuture.supplyAsync(() -> rewardServiceProxy.getAllRewards(user.getUserId()).size()
            ,testExecutor))
        .collect(Collectors.toList());

    for ( CompletableFuture<Integer> rewardSize : rewardListSize) {
      assertTrue(rewardSize.join() > 0);
    }

    System.out.println("highVolumeGetRewards: Time Elapsed: " +
        TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");

    assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
  }

}
