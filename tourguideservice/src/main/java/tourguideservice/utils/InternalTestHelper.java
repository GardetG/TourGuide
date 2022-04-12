package tourguideservice.utils;

import feign.RetryableException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import shared.dto.LocationDto;
import shared.dto.UserDto;
import shared.dto.VisitedLocationDto;
import shared.exception.UserNameAlreadyUsedException;
import tourguideservice.proxy.LocationServiceProxy;
import tourguideservice.proxy.UserServiceProxy;

/**
 * Configuration Class to handle generation of the internal user map when using internal users for
 * testing purposes.
 */
@Profile("test")
@Component
public class InternalTestHelper {

  private static final Logger LOGGER = LoggerFactory.getLogger(InternalTestHelper.class);
  private final UserServiceProxy userServiceProxy;
  private final LocationServiceProxy locationServiceProxy;
  private final Random random = new Random();
  private AtomicInteger count;

  @Autowired
  public InternalTestHelper(UserServiceProxy userServiceProxy,
                            LocationServiceProxy locationServiceProxy) {
    this.userServiceProxy = userServiceProxy;
    this.locationServiceProxy = locationServiceProxy;
  }

  /**
   * Initialize internal users for test purposes according to the provided desired number of users
   * to generate.
   *
   * @param internalUserNumber number of user to generate
   */
  public void initializeInternalUsers(int internalUserNumber) {
    LOGGER.debug("Initialize internal test users.");
    count = new AtomicInteger();
    Executor internalUserExecutor = Executors.newFixedThreadPool(200);
    List<CompletableFuture<Void>> userFutur = IntStream.range(0, internalUserNumber)
        .mapToObj(i ->
            CompletableFuture.runAsync(() -> setUpUser(i, internalUserNumber),
                internalUserExecutor))
        .collect(Collectors.toList());
    userFutur.forEach(CompletableFuture::join);
    LOGGER.debug("Created {} internal test users.", internalUserNumber);
  }

  private void setUpUser(int index, int internalUserNumber) {
    try {
      UUID userid = generateUser(index);
      generateUserLocationHistory(userid);
      logProgress(internalUserNumber);
    } catch (UserNameAlreadyUsedException e) {
      LOGGER.warn("Internal test users creation : Duplicate name");
    } catch (RetryableException e) {
      LOGGER.warn("Internal test users creation : Service unavailable");
    }
  }

  private UUID generateUser(int index) throws UserNameAlreadyUsedException {
    String userName = "internalUser" + index;
    String phone = "000";
    String email = userName + "@tourGuide.com";
    UserDto user = new UserDto(null, userName, phone, email);
    return userServiceProxy.addUser(user).getUserId();
  }

  private void generateUserLocationHistory(UUID userId) {
    List<VisitedLocationDto> visitedLocationDtos = IntStream.range(0, 3)
        .mapToObj(i -> new VisitedLocationDto(
            userId,
            new LocationDto(generateRandomLatitude(), generateRandomLongitude()),
            getRandomTime()
        ))
        .collect(Collectors.toList());
    locationServiceProxy.addVisitedLocation(visitedLocationDtos, userId);
  }

  private void logProgress(double internalUserNumber) {
    double percent = (count.incrementAndGet() / internalUserNumber) * 100;
    if (percent % 10 == 0) {
      LOGGER.debug("Initializing : {} %", percent);
    }
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
