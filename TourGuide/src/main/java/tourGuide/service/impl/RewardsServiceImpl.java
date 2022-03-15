package tourGuide.service.impl;

import java.util.List;

import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
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
import tourGuide.utils.UserRewardMapper;

@Service
public class RewardsServiceImpl implements RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;

	// proximity in miles
    private int defaultProximityBuffer = 10;
	private int proximityBuffer = defaultProximityBuffer;
	private int attractionProximityRange = 200;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;
	private final RewardsRepository rewardsRepository;
	
	public RewardsServiceImpl(GpsUtil gpsUtil, RewardCentral rewardCentral,
							  RewardsRepository rewardsRepository) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
		this.rewardsRepository = rewardsRepository;
	}
	
	@Override
	public void setProximityBuffer(int proximityBuffer) {
		this.proximityBuffer = proximityBuffer;
	}

	public void setDefaultProximityBuffer() {
		proximityBuffer = defaultProximityBuffer;
	}
	
	@Override
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();
		
		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().filter(r -> r.attraction.attractionName.equals(attraction.attractionName)).count() == 0) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction.attractionId, user.getUserId())));
					}
				}
			}
		}
	}
	
	@Override
	public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
		return getDistance(attraction, location) > attractionProximityRange ? false : true;
	}
	
	private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
		return getDistance(attraction, visitedLocation.location) > proximityBuffer ? false : true;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRewardPoints(UUID attractionId, UUID userId) {
		return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
	}
	
	@Override
	public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                               + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        double statuteMiles = STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
        return statuteMiles;
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

	}

}
