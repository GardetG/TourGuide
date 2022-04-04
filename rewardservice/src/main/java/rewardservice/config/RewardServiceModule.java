package rewardservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

@Configuration
public class RewardServiceModule {

  @Bean
  public RewardCentral getTripPricer() {
    return new RewardCentral();
  }

}
