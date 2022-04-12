package tripservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tripPricer.TripPricer;

/**
 * Module Class to return a TripPricer Bean.
 */
@Configuration
public class TripPricerModule {

  @Bean
  public TripPricer getTripPricer() {
    return new TripPricer();
  }

}