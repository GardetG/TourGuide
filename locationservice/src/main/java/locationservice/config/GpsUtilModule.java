package locationservice.config;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GpsUtilModule {

  @Bean
  public GpsUtil getTripPricer() {
    return new GpsUtil();
  }

}