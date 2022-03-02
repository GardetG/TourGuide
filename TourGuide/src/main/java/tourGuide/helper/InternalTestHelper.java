package tourGuide.helper;

import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import tourGuide.domain.User;
import tourGuide.repository.UserRepository;

/**
 * Configuration Class to handle generation of the internal user map when using internal users for
 * testing purposes.
 */
@Profile("internalUser")
@Configuration
@ConfigurationProperties(prefix = "tourguide.internaluser")
public class InternalTestHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(InternalTestHelper.class);
	private final UserRepository userRepository;
	private final Random random = new Random();

	@Autowired
	public InternalTestHelper(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public void setInternalUserNumber(int internalUserNumber) {
		initializeInternalUsers(internalUserNumber);
	}

	public void initializeInternalUsers(int internalUserNumber) {
		userRepository.deleteAll();
		IntStream.range(0, internalUserNumber).forEach(i -> {
			String userName = "internalUser" + i;
			String phone = "000";
			String email = userName + "@tourGuide.com";
			User user = new User(UUID.randomUUID(), userName, phone, email);
			generateUserLocationHistory(user);

			userRepository.save(user);
		});
		LOGGER.debug("Created {} internal test users.", internalUserNumber);
	}

	private void generateUserLocationHistory(User user) {
		IntStream.range(0, 3).forEach(i-> user.addToVisitedLocations(new VisitedLocation(
				user.getUserId(),
				new Location(generateRandomLatitude(), generateRandomLongitude()),
				getRandomTime()
		)));
	}

	private double generateRandomLongitude() {
		double leftLimit = -180;
		double rightLimit = 180;
		return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
	}

	private double generateRandomLatitude() {
		double leftLimit = -85.05112878;
		double rightLimit = 85.05112878;
		return leftLimit + random.nextDouble() * (rightLimit - leftLimit);
	}

	private Date getRandomTime() {
		LocalDateTime localDateTime = LocalDateTime.now().minusDays(random.nextInt(30));
		return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
	}

}
