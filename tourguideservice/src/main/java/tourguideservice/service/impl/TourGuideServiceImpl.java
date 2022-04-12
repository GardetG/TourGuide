package tourguideservice.service.impl;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import shared.dto.AttractionDto;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.LocationDto;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import shared.dto.UserRewardDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;
import shared.exception.UserNotFoundException;
import tourguideservice.dto.NearbyAttractionDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import tourguideservice.proxy.LocationServiceProxy;
import tourguideservice.proxy.RewardServiceProxy;
import tourguideservice.proxy.TripServiceProxy;
import tourguideservice.proxy.UserServiceProxy;
import tourguideservice.service.TourGuideService;
import tourguideservice.utils.NearbyAttractionMapper;

/**
 * Service implementation class for the main service of TourGuide.
 */
@Service
public class TourGuideServiceImpl implements TourGuideService {

  private static final Logger LOGGER = LoggerFactory.getLogger(TourGuideServiceImpl.class);

  private final LocationServiceProxy locationServiceProxy;
  private final RewardServiceProxy rewardServiceProxy;
  private final TripServiceProxy tripServiceProxy;
  private final UserServiceProxy userServiceProxy;

  /**
   * Constructor method for TourGuideService.
   *
   * @param locationServiceProxy LocationService Proxy
   * @param rewardServiceProxy   RewardService Proxy
   * @param tripServiceProxy     TripService Proxy
   * @param userServiceProxy     UserService Proxy
   */
  public TourGuideServiceImpl(LocationServiceProxy locationServiceProxy,
                              RewardServiceProxy rewardServiceProxy,
                              TripServiceProxy tripServiceProxy,
                              UserServiceProxy userServiceProxy) {
    this.locationServiceProxy = locationServiceProxy;
    this.rewardServiceProxy = rewardServiceProxy;
    this.tripServiceProxy = tripServiceProxy;
    this.userServiceProxy = userServiceProxy;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public LocationDto getUserLocation(String userName) throws UserNotFoundException {
    UUID userId = getUserId(userName);
    VisitedLocationDto visitedLocation = getLastVisitedLocation(userId);
    return visitedLocation.getLocation();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<UUID, LocationDto> getAllCurrentLocations() {
    return locationServiceProxy.getAllUserLastVisitedLocation()
        .stream()
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
    UUID userId = getUserId(userName);
    return rewardServiceProxy.getAllRewards(userId);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreferencesDto getUserPreferences(String username) throws UserNotFoundException {
    return userServiceProxy.getUserPreferences(username);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public PreferencesDto setUserPreferences(String username, PreferencesDto userPreferences)
      throws UserNotFoundException {
    return userServiceProxy.setUserPreferences(username, userPreferences);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ProviderDto> getTripDeals(String userName) throws UserNotFoundException {
    UUID userId = getUserId(userName);
    UUID attractionId = getClosestAttraction(userId).getAttractionId();
    PreferencesDto preferences = userServiceProxy.getUserPreferences(userName);
    int rewardPoints = rewardServiceProxy.getTotalRewardPoints(userId);

    return tripServiceProxy.getTripDeals(
        attractionId,
        preferences,
        rewardPoints
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public NearbyAttractionsListDto getNearByAttractions(String userName)
      throws UserNotFoundException {
    UUID userId = getUserId(userName);
    LocationDto userLocation = getLastVisitedLocation(userId).getLocation();
    List<AttractionWithDistanceDto> nearbyAttractions = retrieveNearbyAttractions(userId, 5);

    List<NearbyAttractionDto> attractions = nearbyAttractions
        .stream()
        .parallel()
        .map(attractionWithDistance -> NearbyAttractionMapper.toDto(
                attractionWithDistance,
                rewardServiceProxy.getRewardPoints(
                    attractionWithDistance.getAttraction().getAttractionId(), userId)
            )
        )
        .collect(Collectors.toList());

    return new NearbyAttractionsListDto(
        new LocationDto(userLocation.getLatitude(), userLocation.getLongitude()),
        attractions
    );
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

  /**
   * {@inheritDoc}
   */
  @Override
  public UUID getUserId(String userName) throws UserNotFoundException {
    return userServiceProxy.getUser(userName).getUserId();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<UUID> getAllUsersId() {
    return userServiceProxy.getAllUserId();
  }

  private VisitedLocationDto getLastVisitedLocation(UUID userId) {
    try {
      return locationServiceProxy.getLastVisitedLocation(userId);
    } catch (NoLocationFoundException e) {
      // If no location found for the user then track user location and return current location
      LOGGER.warn("User {} location not found, track current location", userId);
      return trackUserLocation(userId);
    }
  }

  private AttractionDto getClosestAttraction(UUID userId) {
    return retrieveNearbyAttractions(userId, 1)
        .stream()
        .map(AttractionWithDistanceDto::getAttraction)
        .collect(Collectors.toList())
        .get(0);
  }

  private List<AttractionWithDistanceDto> retrieveNearbyAttractions(UUID userId, int limit) {
    try {
      return locationServiceProxy.getNearbyAttractions(userId, limit);
    } catch (NoLocationFoundException e) {
      // If no location found then track the user before retrying
      LOGGER.warn("User {} location not found, track current location", userId);
      trackUserLocation(userId);
    }

    try {
      return locationServiceProxy.getNearbyAttractions(userId, limit);
    } catch (NoLocationFoundException e) {
      LOGGER.error("Unable to retrieve user {} nearby attractions", userId);
      throw new IllegalStateException("Unable to retrieve user nearby attractions");
    }
  }

}
