package tourGuide.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Configuration Class for TourGuide management and tests properties:
 *  - tripPricerApiKey : Api Key used required by TripPricer library
 *  - trackingPollingInterval : Tracking polling interval in minutes
 *  - trackerOnStart : Start the tracker when application starts
 */
@Configuration
@ConfigurationProperties(prefix = "tourguide")
public class TourGuideProperties {

  public static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

  private String tripPricerApiKey;
  private long trackingPollingInterval;
  private boolean trackingOnStart;

  public String getTripPricerApiKey() {
    return tripPricerApiKey;
  }

  public void setTripPricerApiKey(String tripPricerApiKey) {
    this.tripPricerApiKey = tripPricerApiKey;
  }

  public long getTrackingPollingInterval() {
    return trackingPollingInterval;
  }

  public void setTrackingPollingInterval(long trackingPollingInterval) {
    this.trackingPollingInterval = trackingPollingInterval;
  }

  public boolean isTrackingOnStart() {
    return trackingOnStart;
  }

  public void setTrackingOnStart(boolean trackingOnStart) {
    this.trackingOnStart = trackingOnStart;
  }

}
