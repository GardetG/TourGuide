package tourGuide.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Configuration Class for TourGuide management and tests properties:
 *  - tripPricerApiKey : Api Key used required by TripPricer library
 */
@Configuration
@ConfigurationProperties(prefix = "tourguide")
public class TourGuideProperties {

  private String tripPricerApiKey;

  public String getTripPricerApiKey() {
    return tripPricerApiKey;
  }

  public void setTripPricerApiKey(String tripPricerApiKey) {
    this.tripPricerApiKey = tripPricerApiKey;
  }

}
