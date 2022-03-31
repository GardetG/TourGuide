package tourguideservice.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import tourguideservice.service.tracker.Tracker;

/**
 * Initialize the application
 */
@Profile("!test")
@Component
public class DefaultApplicationInit implements ApplicationRunner {

  @Autowired
  private Tracker tracker;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    tracker.startTracking();
  }
}
