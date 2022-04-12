package rewardservice.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;
import rewardservice.domain.UserReward;
import rewardservice.repository.RewardsRepository;
import rewardservice.service.RewardsService;
import rewardservice.utils.AttractionMapper;
import rewardservice.utils.VisitedLocationMapper;
import shared.dto.UserRewardDto;
import shared.dto.VisitedAttractionDto;

/**
 * Service Class implementation to calculate and retrieve users rewards.
 */
@Service
public class RewardsServiceImpl implements RewardsService {

  private final RewardCentral rewardsCentral;
  private final RewardsRepository rewardsRepository;

  @Autowired
  public RewardsServiceImpl(RewardCentral rewardCentral,
                            RewardsRepository rewardsRepository) {
    this.rewardsCentral = rewardCentral;
    this.rewardsRepository = rewardsRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserRewardDto> getAllRewards(UUID userId) {
    return rewardsRepository.findById(userId)
        .stream()
        .map(userReward -> new UserRewardDto(
            VisitedLocationMapper.toDto(userReward.visitedLocation),
            AttractionMapper.toDto(userReward.attraction),
            userReward.getRewardPoints()
        ))
        .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getTotalRewardPoints(UUID userId) {
    return rewardsRepository.findById(userId)
        .stream()
        .mapToInt(UserReward::getRewardPoints)
        .sum();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateRewards(UUID userId,
                               List<VisitedAttractionDto> visitedAttractionsToReward) {
    List<UserReward> rewards = visitedAttractionsToReward
        .stream()
        .map(visitedAttraction -> new UserReward(
            VisitedLocationMapper.toEntity(visitedAttraction.getVisitedLocation()),
            AttractionMapper.toEntity(visitedAttraction.getAttraction()),
            getRewardPoints(visitedAttraction.getAttraction().getAttractionId(),
                visitedAttraction.getVisitedLocation()
                    .getUserId())
        ))
        .collect(Collectors.toList());
    rewards.forEach(rewardsRepository::save);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getRewardPoints(UUID attractionId, UUID userId) {
    return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
  }

}
