package rewardservice.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;
import rewardservice.domain.UserReward;
import rewardservice.repository.RewardsRepository;

/**
 * Repository Class for User rewards.
 * Database connection will be used for external users rewards, but for testing purposes internal
 * users rewards are provided and stored in memory by this implementation.
 */
@Repository
public class RewardsRepositoryImpl implements RewardsRepository {

  private final ConcurrentMap<UUID, List<UserReward>> internalUserRewardsMap =
      new ConcurrentHashMap<>();

  @Override
  public List<UserReward> findById(UUID userId) {
    return internalUserRewardsMap.getOrDefault(userId, new ArrayList<>());
  }

  @Override
  public UserReward save(UserReward reward) {
    UUID userId = reward.visitedLocation.userId;
    List<UserReward> userRewards = findById(userId);
    if (userRewards.stream()
        .noneMatch(r -> r.attraction.attractionName.equals(reward.attraction.attractionName))) {
      userRewards.add(reward);
      internalUserRewardsMap.put(userId, userRewards);
    }
    return reward;
  }

}
