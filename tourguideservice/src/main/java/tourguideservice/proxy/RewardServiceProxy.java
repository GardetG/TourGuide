package tourguideservice.proxy;

import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shared.dto.UserRewardDto;
import shared.dto.VisitedAttractionDto;
import tourguideservice.config.CustomFeignClientConfiguration;

@Service
@FeignClient(value = "reward-service", url = "${tourguide.rewardservice.url}", configuration = CustomFeignClientConfiguration.class)
public interface RewardServiceProxy {

  @GetMapping("/getAllRewards")
  List<UserRewardDto> getAllRewards(@RequestParam UUID userId);

  @GetMapping("/getTotalRewardPoints")
  int getTotalRewardPoints(@RequestParam UUID userId);

  @PostMapping("/calculateRewards")
  void calculateRewards(@RequestParam UUID userId, @RequestBody
      List<VisitedAttractionDto> visitedAttractionsToReward);

  @GetMapping("/getRewardPoints")
  int getRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId);

}
