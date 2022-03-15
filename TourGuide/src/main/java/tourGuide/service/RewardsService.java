package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.dto.VisitedLocationDto;

/**
 * Service Interface to calculate and retrieve users rewards.
 */
@Service
public interface RewardsService {
  void setProximityBuffer(int proximityBuffer);

  void calculateRewards(User user);

  boolean isWithinAttractionProximity(Attraction attraction, Location location);

  double getDistance(Location loc1, Location loc2);

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
  void calculateRewards(UUID userId, Map<AttractionDto, VisitedLocationDto> visitedAttractionsToReward);

  /**
   * Return the number of reward points earned when the user visit the attraction.
   *
   * @param attractionId of the attraction
   * @param userId of the user
   * @return reward points
   */
  int getRewardPoints(UUID attractionId, UUID userId);

}
