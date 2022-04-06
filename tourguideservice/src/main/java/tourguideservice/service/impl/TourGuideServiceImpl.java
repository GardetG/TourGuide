package tourguideservice.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import shared.dto.VisitedAttractionDto;
import tourguideservice.domain.User;
import tourguideservice.domain.UserPreferences;
import shared.dto.AttractionDto;
import shared.dto.LocationDto;
import tourguideservice.dto.NearbyAttractionDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import shared.dto.UserRewardDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;
import shared.exception.UserNotFoundException;
import tourguideservice.repository.UserRepository;
import tourguideservice.service.TourGuideService;
import tourguideservice.service.proxy.LocationServiceProxy;
import tourguideservice.service.proxy.RewardServiceProxy;
import tourguideservice.service.proxy.TripServiceProxy;
import tourguideservice.utils.PreferencesMapper;
import tourguideservice.utils.ProviderMapper;

/**
 * Service implementation class for the main service of TourGuide.
 */
@Service
public class TourGuideServiceImpl implements TourGuideService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TourGuideServiceImpl.class);

  private final LocationServiceProxy locationServiceProxy;
  private final RewardServiceProxy rewardServiceProxy;
  private final TripServiceProxy tripServiceProxy;
  private final UserRepository userRepository;

  public TourGuideServiceImpl(LocationServiceProxy locationServiceProxy,
                              RewardServiceProxy rewardServiceProxy,
                              TripServiceProxy tripServiceProxy, UserRepository userRepository) {
    this.locationServiceProxy = locationServiceProxy;
    this.rewardServiceProxy = rewardServiceProxy;
    this.tripServiceProxy = tripServiceProxy;
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
    return locationServiceProxy.getAllUserLastVisitedLocation().stream()
        .collect(Collectors.toMap(
            VisitedLocationDto::getUserId,
            VisitedLocationDto::getLocation
        ));
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UserRewardDto> getUserRewards(String userName) throws UserNotFoundException {
    UUID userId = getUser(userName).getUserId();
    return rewardServiceProxy.getAllRewards(userId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreferencesDto getUserPreferences(String username) throws UserNotFoundException {
    User user = getUser(username);
    return PreferencesMapper.toDto(user.getUserPreferences());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreferencesDto setUserPreferences(String username, PreferencesDto userPreferences)
      throws UserNotFoundException {
    User user = getUser(username);
    user.setUserPreferences(PreferencesMapper.toEntity(userPreferences));
    return userPreferences;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ProviderDto> getTripDeals(String userName) throws UserNotFoundException {
    User user = getUser(userName);
    UserPreferences preferences = user.getUserPreferences();
    UUID attractionId = getClosestAttraction(user.getUserId()).getAttractionId();
    int rewardPoints = rewardServiceProxy.getTotalRewardPoints(user.getUserId());
    List<ProviderDto> providers = tripServiceProxy.getTripDeals(
        attractionId,
        PreferencesMapper.toDto(preferences),
        rewardPoints
    );
    user.setTripDeals(ProviderMapper.toEntity(providers));
    return providers;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NearbyAttractionsListDto getNearByAttractions(String userName)
      throws UserNotFoundException {
    UUID userId = getUser(userName).getUserId();
    LocationDto userLocation = getLastVisitedLocation(userId).getLocation();
    List<AttractionWithDistanceDto> nearbyAttractions = retrieveNearbyAttractions(userId, 5);
    List<NearbyAttractionDto> attractions = nearbyAttractions
        .stream()
        .parallel()
        .map(attractionWithDistance -> new NearbyAttractionDto(
            attractionWithDistance.getAttraction().getAttractionName(),
            attractionWithDistance.getAttraction().getLatitude(),
            attractionWithDistance.getAttraction().getLongitude(),
            attractionWithDistance.getDistance(),
            rewardServiceProxy.getRewardPoints(
                attractionWithDistance.getAttraction().getAttractionId(), userId)
        ))
        .collect(Collectors.toList());
    return new NearbyAttractionsListDto(
        new LocationDto(userLocation.getLatitude(), userLocation.getLongitude()),
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

  /**
   * {@inheritDoc}
   */
  @Override
  public VisitedLocationDto trackUserLocation(UUID userId) {
    return locationServiceProxy.trackUserLocation(userId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void calculateRewards(UUID userId) {
    List<VisitedAttractionDto> attractionToReward =
        locationServiceProxy.getVisitedAttractions(userId);
    rewardServiceProxy.calculateRewards(userId, attractionToReward);
  }

  public void addUser(User user) {
    userRepository.save(user);
  }

  private VisitedLocationDto getLastVisitedLocation(UUID userId) {
    try {
      return locationServiceProxy.getLastVisitedLocation(userId);
    } catch (NoLocationFoundException e) {
      LOGGER.warn("User {} location not found, track current location", userId);
      return trackUserLocation(userId);
    }
  }

  private AttractionDto getClosestAttraction(UUID userId) {
    return retrieveNearbyAttractions(userId, 1)
        .stream()
        .map(AttractionWithDistanceDto::getAttraction)
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Unable to retrieve user {} closest attraction", userId);
          return new IllegalStateException("Unable to retrieve user closest attraction");
        });
  }

  private List<AttractionWithDistanceDto> retrieveNearbyAttractions(UUID userId, int limit) {
    try {
      return locationServiceProxy.getNearbyAttractions(userId, limit);
    } catch (NoLocationFoundException e) {
      LOGGER.warn("User {} location not found, track current location", userId);
      trackUserLocation(userId);
    }

    try {
      return locationServiceProxy.getNearbyAttractions(userId, limit);
    } catch (NoLocationFoundException e) {
      LOGGER.error("Unable to retrieve user {} nearby attractions", userId);
      throw new IllegalArgumentException("Unable to retrieve user nearby attractions");
    }
  }

}
