package tourGuide.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import gpsUtil.GpsUtil;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.time.StopWatch;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import tourGuide.domain.User;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.LocationDto;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.tracker.Tracker;

@SpringBootTest(properties = "tourguide.internaluser.internalUserNumber=100")
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
	@Autowired
	private Tracker tracker;

	@BeforeAll
	public static void setDefaultLocale() {
		Locale.setDefault(Locale.UK);
	}

	@Test
	void highVolumeTrackLocation() {
		// Giveb
		List<User> allUsers = tourGuideService.getAllUsers();
		tracker.startTracking();

		// When
	    StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		for(User user : allUsers) {
			tourGuideService.trackUserLocation(user.getUserId());
		}
		stopWatch.stop();

		// Then
		tracker.stopTracking();
		System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

	@Test
	void highVolumeGetRewards() {
		// Giveb
		GpsUtil gpsUtil = new GpsUtil();
		List<User> allUsers = tourGuideService.getAllUsers();
		tracker.startTracking();

		// When
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		AttractionDto attraction = gpsService.getAttraction().get(0);
		allUsers.forEach(u -> gpsService.addLocation(
				u.getUserId(),
				new LocationDto(attraction.getLongitude(), attraction.getLatitude())
		));
	     
	    allUsers.forEach(u -> rewardsService.calculateRewards(u));
		for(User user : allUsers) {
			assertTrue(rewardsService.getAllRewards(user.getUserId()).size() > 0);
		}
		stopWatch.stop();

		// Then
		tracker.stopTracking();
		System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds."); 
		assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
	}

}
