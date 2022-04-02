package tourguideservice.service.helper;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tourguideservice.domain.User;
import shared.dto.LocationDto;
import shared.dto.VisitedLocationDto;
import tourguideservice.repository.UserRepository;
import tourguideservice.service.proxy.LocationServiceProxy;

/**
 * Configuration Class to handle generation of the internal user map when using internal users for
 * testing purposes.
 */
@Profile("test")
@Component
public class InternalTestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(InternalTestHelper.class);
  private final UserRepository userRepository;
  private final LocationServiceProxy locationServiceProxy;
  private final Random random = new Random();

  @Autowired
  public InternalTestHelper(UserRepository userRepository,
                            LocationServiceProxy locationServiceProxy) {
    this.userRepository = userRepository;
    this.locationServiceProxy = locationServiceProxy;
  }

  public void initializeInternalUsers(int internalUserNumber) {
    userRepository.deleteAll();
    Executor internalUserExecutor = Executors.newFixedThreadPool(200);
    List<CompletableFuture<Void>> userFutur = IntStream.range(0, internalUserNumber)
        .mapToObj(i -> CompletableFuture.runAsync(() -> generateUser(i), internalUserExecutor))
        .collect(Collectors.toList());
    userFutur.forEach(CompletableFuture::join);
    LOGGER.debug("Created {} internal test users.", internalUserNumber);
  }

  private void generateUser(int index) {
    String userName = "internalUser" + index;
    String phone = "000";
    String email = userName + "@tourGuide.com";
    User user = new User(UUID.randomUUID(), userName, phone, email);
    generateUserLocationHistory(user);
    userRepository.save(user);
  }

  private void generateUserLocationHistory(User user) {
    List<VisitedLocationDto> visitedLocationDtos = IntStream.range(0, 3)
        .mapToObj(i -> new VisitedLocationDto(
            user.getUserId(),
            new LocationDto(generateRandomLatitude(), generateRandomLongitude()),
            getRandomTime()
        ))
        .collect(Collectors.toList());
    locationServiceProxy.addVisitedLocation(visitedLocationDtos, user.getUserId());
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
