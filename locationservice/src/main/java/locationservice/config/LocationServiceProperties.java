package locationservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Configuration Class for TourGuide management and tests properties:
 *  - proximityDistanceInMiles : Api Key used required by TripPricer library
 */
@Configuration
@ConfigurationProperties(prefix = "locationservice")
public class LocationServiceProperties {

  public static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

  private int proximityThresholdInMiles;

  public int getProximityThresholdInMiles() {
    return proximityThresholdInMiles;
  }

  public void setProximityThresholdInMiles(int proximityThresholdInMiles) {
    this.proximityThresholdInMiles = proximityThresholdInMiles;
  }

}
