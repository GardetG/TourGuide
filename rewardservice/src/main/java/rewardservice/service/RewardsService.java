package rewardservice.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import shared.dto.UserRewardDto;
import shared.dto.VisitedAttractionDto;

/**
 * Service Interface to calculate and retrieve users rewards.
 */
@Service
public interface RewardsService {

  /**
   * Get the list of all user rewards.
   *
   * @param userId of the user
   * @return list of user reward Dto
   */
  List<UserRewardDto> getAllRewards(UUID userId);

  /**
   * Get the total of reward points earned by the user.
   *
   * @param userId of the user
   * @return total reward points
   */
  int getTotalRewardPoints(UUID userId);

  /**
   * Calculate the user rewards from a map of attraction to reward and the associated user visited
   * location in range, and registered the rewards.
   *
   * @param userId of the user
   * @param visitedAttractionsToReward attractions to reward
   */
  void calculateRewards(UUID userId, List<VisitedAttractionDto> visitedAttractionsToReward);

  /**
   * Return the number of reward points earned when the user visit the attraction.
   *
   * @param attractionId of the attraction
   * @param userId of the user
   * @return reward points
   */
  int getRewardPoints(UUID attractionId, UUID userId);

}
