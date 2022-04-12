package tripservice.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 *  Configuration Class for TripService properties:
 *  - tripPricerApiKey : Api Key used required by TripPricer library.
 */
@Configuration
@ConfigurationProperties(prefix = "tripservice")
public class TripServiceProperties {

  private String tripPricerApiKey;

  public String getTripPricerApiKey() {
    return tripPricerApiKey;
  }

  public void setTripPricerApiKey(String tripPricerApiKey) {
    this.tripPricerApiKey = tripPricerApiKey;
  }

}
