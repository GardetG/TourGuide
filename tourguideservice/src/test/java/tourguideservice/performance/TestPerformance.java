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
import tourguideservice.dto.AttractionDto;
import tourguideservice.dto.LocationDto;
import tourguideservice.dto.VisitedLocationDto;
import tourguideservice.service.GpsService;
import tourguideservice.service.RewardsService;
import tourguideservice.service.TourGuideService;

@Tag("performance")
@SpringBootTest(properties = "tourguide.internaluser.internalUserNumber=100000")
@ActiveProfiles({"test", "internalUser"})
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
	private GpsService gpsService;
	@Autowired
	private RewardsService rewardsService;

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
				.map(user -> CompletableFuture.runAsync(() -> tourGuideService.trackUserLocation(user.getUserId()), executor))
				.collect(Collectors.toList());
		result.forEach(CompletableFuture::join);
		stopWatch.stop();

		// Then
		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	void highVolumeGetRewards() {
		// Given
		List<User> allUsers = tourGuideService.getAllUsers();

		// When
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		AttractionDto attraction = gpsService.getAttraction().get(0);
		allUsers.forEach(u -> gpsService.addLocation(new VisitedLocationDto(
				u.getUserId(),
				new LocationDto(attraction.getLongitude(), attraction.getLatitude()),
				new Date()
		)));

		ExecutorService executor = Executors.newFixedThreadPool(200);
		List<CompletableFuture<?>> result = allUsers.stream()
				.map(user -> CompletableFuture.runAsync(() -> tourGuideService.calculateRewards(user.getUserId()), executor))
				.collect(Collectors.toList());
		result.forEach(CompletableFuture::join);

		for(User user : allUsers) {
			assertTrue(rewardsService.getAllRewards(user.getUserId()).size() > 0);
		}
		stopWatch.stop();

		// Then

		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
