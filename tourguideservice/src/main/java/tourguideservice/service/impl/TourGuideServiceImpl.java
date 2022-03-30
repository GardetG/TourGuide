package tourguideservice.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import tourguideservice.domain.User;
import tourguideservice.domain.UserPreferences;
import tourguideservice.dto.AttractionDto;
import tourguideservice.dto.LocationDto;
import tourguideservice.dto.NearbyAttractionDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import tourguideservice.dto.UserRewardDto;
import tourguideservice.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;
import tourguideservice.exception.UserNotFoundException;
import tourguideservice.repository.UserRepository;
import tourguideservice.service.GpsService;
import tourguideservice.service.RewardsService;
import tourguideservice.service.TourGuideService;
import tourguideservice.service.proxy.TripServiceProxy;
import tourguideservice.utils.PreferencesMapper;
import tourguideservice.utils.ProviderMapper;

/**
 * Service implementation class for the main service of TourGuide.
 */
@Service
public class TourGuideServiceImpl implements TourGuideService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TourGuideServiceImpl.class);

  private final GpsService gpsService;
  private final RewardsService rewardsService;
  private final TripServiceProxy tripDealsService;
  private final UserRepository userRepository;

  public TourGuideServiceImpl(GpsService gpsService, RewardsService rewardsService,
                              TripServiceProxy tripDealsService, UserRepository userRepository) {
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
    UUID userId = getUser(userName).getUserId();
    return rewardsService.getAllRewards(userId);
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
    int rewardPoints = rewardsService.getTotalRewardPoints(user.getUserId());
    List<ProviderDto> providers = tripDealsService.getTripDeals(
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
  public NearbyAttractionsListDto getNearByAttractions(String userName) throws UserNotFoundException {
    UUID userId = getUser(userName).getUserId();
    LocationDto userLocation = getLastVisitedLocation(userId).getLocation();
    Map<AttractionDto, Double> attractionsMap = retrieveNearbyAttractions(userId, 5);
    List<NearbyAttractionDto> attractions = attractionsMap.keySet()
        .stream()
        .parallel()
        .map(attraction -> new NearbyAttractionDto(
            attraction.getAttractionName(),
            attraction.getLatitude(),
            attraction.getLongitude(),
            attractionsMap.get(attraction),
            rewardsService.getRewardPoints(attraction.getAttractionId(), userId)
        ))
        .collect(Collectors.toList());
    return new NearbyAttractionsListDto(
        new LocationDto(userLocation.getLongitude(), userLocation.getLatitude()),
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

  private VisitedLocationDto getLastVisitedLocation(UUID userId) {
    try {
      return gpsService.getLastLocation(userId);
    } catch (NoLocationFoundException e) {
      LOGGER.warn("User {} location not found, track current location", userId);
      return trackUserLocation(userId);
    }
  }

  private AttractionDto getClosestAttraction(UUID userId) {
    return retrieveNearbyAttractions(userId, 1)
        .entrySet()
        .stream()
        .findFirst()
        .orElseThrow(() -> {
          LOGGER.error("Unable to retrieve user {} closest attraction", userId);
          return new IllegalStateException("Unable to retrieve user closest attraction");
        })
        .getKey();
  }

  private Map<AttractionDto, Double> retrieveNearbyAttractions(UUID userId, int limit) {
    try {
      return gpsService.getNearbyAttractions(userId, limit);
    } catch (NoLocationFoundException e) {
      LOGGER.warn("User {} location not found, track current location", userId);
      trackUserLocation(userId);
    }

    try {
      return gpsService.getNearbyAttractions(userId, limit);
    } catch (NoLocationFoundException e) {
      LOGGER.error("Unable to retrieve user {} nearby attractions", userId);
      throw new IllegalArgumentException("Unable to retrieve user nearby attractions");
    }
  }

}
