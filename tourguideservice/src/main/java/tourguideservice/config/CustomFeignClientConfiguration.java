package tourguideservice.config;

import feign.Logger;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import tourguideservice.proxy.FeignCustomErrorDecoder;

/**
 * Configuration Class for Feign Client.
 */
@Configuration
public class CustomFeignClientConfiguration {

  @Bean
  public ErrorDecoder errorDecoder() {
    return new FeignCustomErrorDecoder();
  }

  @Bean
  Logger.Level feignLoggerLevel() {
    return Logger.Level.NONE;
  }

}