package tourGuide.service.impl;

import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.domain.User;
import tourGuide.domain.UserReward;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.repository.RewardsRepository;
import tourGuide.service.RewardsService;
import tourGuide.utils.AttractionMapper;
import tourGuide.utils.UserRewardMapper;
import tourGuide.utils.VisitedLocationMapper;

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
				.map(UserRewardMapper::toDto)
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
								 Map<AttractionDto, VisitedLocationDto> visitedAttractionsToReward) {
		List<UserReward> rewards =  visitedAttractionsToReward.entrySet()
				.stream()
				.map(entry -> new UserReward(
						VisitedLocationMapper.toEntity(entry.getValue()),
						AttractionMapper.toEntity(entry.getKey()),
						getRewardPoints(entry.getKey().getAttractionId(), entry.getValue()
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
