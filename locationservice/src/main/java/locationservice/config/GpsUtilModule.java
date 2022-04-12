package locationservice.config;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Module Class to return a GpsUtils Bean.
 */
@Configuration
public class GpsUtilModule {

  @Bean
  public GpsUtil getTripPricer() {
    return new GpsUtil();
  }

}