package tourGuide.service.impl;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.domain.UserPreferences;
import tourGuide.domain.UserReward;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.UserRepository;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripDealsService;
import tourGuide.utils.ProviderMapper;
import tripPricer.Provider;

/**
 * Service implementation class for the main service of TourGuide.
 */
@Service
public class TourGuideServiceImpl implements TourGuideService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TourGuideServiceImpl.class);

	private final GpsUtil gpsUtil;
	private final RewardsService rewardsService;
	private final TripDealsService tripDealsService;
	private final UserRepository userRepository;

	public TourGuideServiceImpl(GpsUtil gpsUtil, RewardsService rewardsService, TripDealsService tripDealsService, UserRepository userRepository) {
		this.gpsUtil = gpsUtil;
		this.rewardsService = rewardsService;
		this.tripDealsService = tripDealsService;
		this.userRepository = userRepository;
	}
	
	@Override
	public List<UserReward> getUserRewards(User user) {
		return user.getUserRewards();
	}
	
	@Override
	public VisitedLocation getUserLocation(User user) {
		VisitedLocation visitedLocation = (user.getVisitedLocations().size() > 0) ?
			user.getLastVisitedLocation() :
			trackUserLocation(user);
		return visitedLocation;
	}
	
	@Override
	public User getUser(String userName) throws UserNotFoundException {
		return userRepository.findByUsername(userName)
				.orElseThrow(() -> {
					LOGGER.error("User not found");
					return new UserNotFoundException("User not found");
				});
	}
	
	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	public void addUser(User user) {
		userRepository.save(user);
	}
	
	@Override
	public List<ProviderDto> getTripDeals(String userName) throws UserNotFoundException {
		User user = getUser(userName);
		UserPreferences preferences = user.getUserPreferences();
		int rewardPoints =  user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
		List<Provider> providers = tripDealsService.getTripDeals(
				user.getUserId(),
				preferences,
				rewardPoints
		);
		user.setTripDeals(providers);
		return ProviderMapper.toDto(providers);
	}

	@Override
	public UserPreferencesDto getUserPreferences(String username) throws UserNotFoundException {
		return null;
	}

	@Override
	public UserPreferencesDto setUserPreferences(String username,
												 UserPreferencesDto userPreferences)
			throws UserNotFoundException {
		return null;
	}

	@Override
	public VisitedLocation trackUserLocation(User user) {
		VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
		user.addToVisitedLocations(visitedLocation);
		rewardsService.calculateRewards(user);
		return visitedLocation;
	}

	@Override
	public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
		List<Attraction> nearbyAttractions = new ArrayList<>();
		for(Attraction attraction : gpsUtil.getAttractions()) {
			if(rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
				nearbyAttractions.add(attraction);
			}
		}
		
		return nearbyAttractions;
	}
	
}
