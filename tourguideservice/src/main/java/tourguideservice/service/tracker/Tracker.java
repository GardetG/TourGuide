package tourguideservice.service.tracker;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourguideservice.config.TourGuideProperties;
import tourguideservice.domain.User;
import tourguideservice.service.TourGuideService;

/**
 * Tracker Class to periodically update users location and reward.
 */
@Service
public class Tracker implements Runnable {

  private static final Logger LOGGER = LoggerFactory.getLogger(Tracker.class);

  private final TourGuideService tourGuideService;
  private final long trackingPollingInterval;
  private ScheduledExecutorService executorService;

  @Autowired
  public Tracker(TourGuideService tourGuideService, TourGuideProperties properties) {
    this.tourGuideService = tourGuideService;
    this.trackingPollingInterval = properties.getTrackingPollingInterval();
    addShutDownHook();
    LOGGER.debug("Tracker Ready and Waiting");
  }

  /**
   * Start the Tracker thread
   */
  public void startTracking() {
    executorService = Executors.newSingleThreadScheduledExecutor();
    executorService.scheduleAtFixedRate(this, 0, trackingPollingInterval, TimeUnit.MINUTES);
    LOGGER.debug("Tracker starting");
  }

  /**
   * Shut down the Tracker thread
   */
  public void stopTracking() {
    if (executorService != null) {
      executorService.shutdownNow();
      LOGGER.debug("Tracker stopping");
    }
  }

  @Override
  public void run() {
    StopWatch stopWatch = new StopWatch();
    List<User> users = tourGuideService.getAllUsers();
    LOGGER.debug("Begin Tracker. Tracking {} users.", users.size());
    stopWatch.start();

    ExecutorService trackExecutor = Executors.newFixedThreadPool(200);
    ExecutorService rewardExecutor = Executors.newFixedThreadPool(200);

    List<CompletableFuture<?>> trackResult = users.stream()
        .map(user -> CompletableFuture.runAsync(() -> tourGuideService.trackUserLocation(user.getUserId()), trackExecutor)
            .thenRunAsync(() -> tourGuideService.calculateRewards(user.getUserId()), rewardExecutor))
        .collect(Collectors.toList());

    trackResult.forEach(CompletableFuture::join);

    stopWatch.stop();
    LOGGER.debug("Tracker Time Elapsed: {} seconds.", TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    stopWatch.reset();
  }

  private void addShutDownHook() {
    Runtime.getRuntime().addShutdownHook(new Thread(this::stopTracking));
  }

}
