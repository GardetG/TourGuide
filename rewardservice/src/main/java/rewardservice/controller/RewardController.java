package rewardservice.controller;

import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import rewardservice.service.RewardsService;
import shared.dto.UserRewardDto;
import shared.dto.VisitedAttractionDto;

/**
 * Controller Class exposing LocationService API end points.
 */
@RestController
public class RewardController {

  private static final Logger LOGGER = LoggerFactory.getLogger(RewardController.class);

  @Autowired
  private RewardsService rewardsService;

  /**
   * Get the list of all user rewards.
   *
   * @param userId of the user
   * @return list of user reward Dto
   */
  @GetMapping("/getAllRewards")
  public List<UserRewardDto> getAllRewards(@RequestParam UUID userId) {
    LOGGER.info("Request: Get user {} all rewards", userId);
    List<UserRewardDto> rewards = rewardsService.getAllRewards(userId);
    LOGGER.info("Response: User {} all rewards sent", userId);
    return rewards;
  }

  /**
   * Get the total of reward points earned by the user.
   *
   * @param userId of the user
   * @return total reward points
   */
  @GetMapping("/getTotalRewardPoints")
  public int getTotalRewardPoints(@RequestParam UUID userId) {
    LOGGER.info("Request: Get total reward points of user {}", userId);
    int total = rewardsService.getTotalRewardPoints(userId);
    LOGGER.info("Response: Total reward points of user {} sent", userId);
    return total;
  }

  /**
   * Calculate the user rewards from a map of attraction to reward and the associated user visited
   * location in range, and registered the rewards.
   *
   * @param userId of the user
   * @param visitedAttractionsToReward attractions to reward
   */
  @PostMapping("/calculateRewards")
  public void calculateRewards(@RequestParam UUID userId, @RequestBody List<VisitedAttractionDto> visitedAttractionsToReward) {
    LOGGER.info("Request: Calculate rewards of user {}", userId);
    rewardsService.calculateRewards(userId, visitedAttractionsToReward);
    LOGGER.info("Response: Rewards of user {} calculated", userId);
  }

  /**
   * Return the number of reward points earned when the user visit the attraction.
   *
   * @param attractionId of the attraction
   * @param userId of the user
   * @return reward points
   */
  @GetMapping("/getRewardPoints")
  public int getRewardPoints(@RequestParam UUID attractionId, @RequestParam UUID userId) {
    LOGGER.info("Request: Get attraction {} reward point for user {}", attractionId, userId);
    int points = rewardsService.getRewardPoints(attractionId, userId);
    LOGGER.info("Response: Attraction {} reward points for user {} sent", attractionId, userId);
    return points;
  }

}
