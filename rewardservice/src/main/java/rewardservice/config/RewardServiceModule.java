package rewardservice.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

/**
 * Module Class to return a RewardCentral Bean.
 */
@Configuration
public class RewardServiceModule {

  @Bean
  public RewardCentral getRewardCentral() {
    return new RewardCentral();
  }

}
