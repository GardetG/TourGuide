package tourguideservice.service.impl;

import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import rewardCentral.RewardCentral;
import tourguideservice.domain.UserReward;
import shared.dto.AttractionDto;
import tourguideservice.dto.UserRewardDto;
import shared.dto.VisitedLocationDto;
import tourguideservice.repository.RewardsRepository;
import tourguideservice.service.RewardsService;
import tourguideservice.utils.AttractionMapper;
import tourguideservice.utils.UserRewardMapper;
import tourguideservice.utils.VisitedLocationMapper;

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
