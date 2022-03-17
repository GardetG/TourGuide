package tourGuide.service.impl;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.domain.UserPreferences;
import tourGuide.domain.UserReward;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionDto;
import tourGuide.dto.NearbyAttractionsListDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.exception.NoLocationFoundException;
import tourGuide.exception.UserNotFoundException;
import tourGuide.repository.UserRepository;
import tourGuide.service.GpsService;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tourGuide.service.TripDealsService;
import tourGuide.utils.ProviderMapper;
import tourGuide.utils.UserPreferencesMapper;
import tourGuide.utils.UserRewardMapper;
import tripPricer.Provider;

/**
 * Service implementation class for the main service of TourGuide.
 */
@Service
public class TourGuideServiceImpl implements TourGuideService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TourGuideServiceImpl.class);

  private final GpsService gpsService;
  private final RewardsService rewardsService;
  private final TripDealsService tripDealsService;
  private final UserRepository userRepository;

  public TourGuideServiceImpl(GpsService gpsService, RewardsService rewardsService,
                              TripDealsService tripDealsService, UserRepository userRepository) {
    this.gpsService = gpsService;
    this.rewardsService = rewardsService;
    this.tripDealsService = tripDealsService;
    this.userRepository = userRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LocationDto getUserLocation(String userName) throws UserNotFoundException {
    UUID userId = getUser(userName).getUserId();
    VisitedLocationDto visitedLocation = getLastVisitedLocation(userId);
    return visitedLocation.getLocation();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<UUID, LocationDto> getAllCurrentLocations() {
    return getAllUsers().stream()
        .collect(Collectors.toMap(
            User::getUserId,
            user -> getLastVisitedLocation(user.getUserId()).getLocation()
        ));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserRewardDto> getUserRewards(String userName) throws UserNotFoundException {
    User user = getUser(userName);
    return UserRewardMapper.toDto(user.getUserRewards());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserPreferencesDto getUserPreferences(String username) throws UserNotFoundException {
    User user = getUser(username);
    return UserPreferencesMapper.toDto(user.getUserPreferences());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UserPreferencesDto setUserPreferences(String username, UserPreferencesDto userPreferences)
      throws UserNotFoundException {
    User user = getUser(username);
    user.setUserPreferences(UserPreferencesMapper.toEntity(userPreferences));
    return userPreferences;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ProviderDto> getTripDeals(String userName) throws UserNotFoundException {
    User user = getUser(userName);
    UserPreferences preferences = user.getUserPreferences();
    int rewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();
    List<Provider> providers = tripDealsService.getTripDeals(
        user.getUserId(),
        preferences,
        rewardPoints
    );
    user.setTripDeals(providers);
    return ProviderMapper.toDto(providers);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NearbyAttractionsListDto getNearByAttractions(String userName) throws UserNotFoundException {
    User user = getUser(userName);
    VisitedLocation userLocation = getLastVisitedLocation(user);
    Map<Attraction, Double> attractionsMap =
        gpsService.getTopNearbyAttractionsWithDistances(userLocation.location, 5);
    List<NearbyAttractionDto> attractions = attractionsMap.keySet()
        .stream()
        .parallel()
        .map(attraction -> new NearbyAttractionDto(
            attraction.attractionName,
            attraction.latitude,
            attraction.longitude,
            attractionsMap.get(attraction),
            rewardsService.getRewardPoints(attraction.attractionId, user.getUserId())
        ))
        .collect(Collectors.toList());
    return new NearbyAttractionsListDto(
        new LocationDto(userLocation.location.longitude, userLocation.location.latitude),
        attractions
    );
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

  @Override
  public VisitedLocation trackUserLocation(User user) {
    VisitedLocation visitedLocation = gpsService.getUserLocation(user.getUserId());
    user.addToVisitedLocations(visitedLocation);
    rewardsService.calculateRewards(user);
    return visitedLocation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VisitedLocationDto trackUserLocation(UUID userId) {
    return gpsService.trackUserLocation(userId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateRewards(UUID userId) {
    Map<AttractionDto, VisitedLocationDto> attractionToReward = gpsService.getVisitedAttractions(userId);
    rewardsService.calculateRewards(userId, attractionToReward);
  }

  public void addUser(User user) {
    userRepository.save(user);
  }

  private VisitedLocation getLastVisitedLocation(User user) {
    return user.getVisitedLocations().isEmpty() ?
        trackUserLocation(user) :
        user.getLastVisitedLocation();
  }

  private VisitedLocationDto getLastVisitedLocation(UUID userId) {
    try {
      return gpsService.getLastLocation(userId);
    } catch (NoLocationFoundException e) {
      LOGGER.warn("User {} location not found, track current location", userId);
      return trackUserLocation(userId);
    }
  }

}
