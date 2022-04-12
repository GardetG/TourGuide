package tourguideservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tourguideservice.service.tracker.Tracker;
import tourguideservice.utils.InternalTestHelper;

/**
 * Initialize the application in test by orchestrate the generation of internal users and the
 * starting of the tracker.
 */
@Profile("test")
@Component
public class TestApplicationInit implements ApplicationRunner {

  @Autowired
  private Tracker tracker;
  @Autowired
  private InternalTestHelper internalTestHelper;

  @Autowired
  private TourGuideTestProperties testProperties;

  @Override
  public void run(ApplicationArguments args) {
    if (testProperties.isUseInternalUser()) {
      internalTestHelper.initializeInternalUsers(testProperties.getInternalUserNumber());
    }
    if (testProperties.isTrackingOnStart()) {
      tracker.startTracking();
    }
  }
}
