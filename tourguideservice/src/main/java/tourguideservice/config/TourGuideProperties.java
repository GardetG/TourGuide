package tourguideservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Configuration Class for TourGuide management and tests properties:
 *  - trackingPollingInterval : Tracking polling interval in minutes
 */
@Configuration
@ConfigurationProperties(prefix = "tourguide")
public class TourGuideProperties {

  public static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
  private long trackingPollingInterval;

  public long getTrackingPollingInterval() {
    return trackingPollingInterval;
  }

  public void setTrackingPollingInterval(long trackingPollingInterval) {
    this.trackingPollingInterval = trackingPollingInterval;
  }

}
