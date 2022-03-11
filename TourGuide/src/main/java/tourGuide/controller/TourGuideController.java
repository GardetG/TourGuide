package tourGuide.controller;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionsListDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.TourGuideService;

/**
 * TourGuide Controller Class exposing TourGuide API end points.
 */
@RestController
public class TourGuideController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TourGuideController.class);

  @Autowired
  TourGuideService tourGuideService;

  @RequestMapping("/")
  public String index() {
    return "Greetings from TourGuide!";
  }

  /**
   * Get the current location of a user by its username.
   *
   * @param userName of the user
   * @return Location of the user
   * @throws UserNotFoundException when user not found
   */
  @RequestMapping("/getLocation")
  public LocationDto getLocation(@RequestParam String userName) throws UserNotFoundException {
    LOGGER.info("Request: User {} location", userName);
    LocationDto location = tourGuideService.getUserLocation(userName);
    LOGGER.info("Response: User {} location sent", userName);
    return location;
  }

  /**
   * Get the list of all user id with their current location.
   *
   * @return map of user Id and location
   */
  @RequestMapping("/getAllCurrentLocations")
  public Map<UUID, LocationDto> getAllCurrentLocations() {
    LOGGER.info("Request: Get All User location");
    Map<UUID, LocationDto> allCurrentLocations = tourGuideService.getAllCurrentLocations();
    LOGGER.info("Request: All User location sent");
    return allCurrentLocations;
  }

  /**
   * Get the list of all rewards of a user by its userName.
   *
   * @param userName of the user
   * @return list of rewards
   * @throws UserNotFoundException when user not found
   */
  @RequestMapping("/getRewards")
  public List<UserRewardDto> getRewards(@RequestParam String userName) throws UserNotFoundException {
    LOGGER.info("Request: Get user {} rewards", userName);
    List<UserRewardDto> rewards = tourGuideService.getUserRewards(userName);
    LOGGER.info("Request: User {} rewards sent", userName);
    return  rewards;
  }

  /**
   * Get user preferences by its userName.
   *
   * @param userName of the user
   * @return user preferences
   * @throws UserNotFoundException when user not found
   */
  @RequestMapping("/getUserPreferences")
  public UserPreferencesDto getUserPreferences(@RequestParam String userName) throws UserNotFoundException {
    LOGGER.info("Request: Get user {} preferences", userName);
    UserPreferencesDto preferences = tourGuideService.getUserPreferences(userName);
    LOGGER.info("Request: User {} preferences sent", userName);
    return preferences;
  }

  /**
   * Update user preferences by its userName and the provided values.
   *
   * @param userName of the user
   * @param userPreferencesDto to update
   * @return updated user preferences
   * @throws UserNotFoundException when user not found
   */
  @PutMapping(value = "/setUserPreferences")
  public UserPreferencesDto getUserPreferences(@RequestParam String userName,
                                   @Valid @RequestBody UserPreferencesDto userPreferencesDto)
      throws UserNotFoundException {
    LOGGER.info("Request: Update user {} preferences", userName);
    UserPreferencesDto preferences = tourGuideService.setUserPreferences(userName, userPreferencesDto);
    LOGGER.info("Request: Update successfully user {} preferences", userName);
    return preferences;
  }

  /**
   * Get user Trip deals according to its userName and the defined preferences.
   *
   * @param userName of the user
   * @return list of providers
   * @throws UserNotFoundException when user not found
   */
  @RequestMapping("/getTripDeals")
  public List<ProviderDto> getTripDeals(@RequestParam String userName) throws UserNotFoundException {
    LOGGER.info("Request: Get user {} trip deals", userName);
    List<ProviderDto> providers = tourGuideService.getTripDeals(userName);
    LOGGER.info("Response: User {} trip deals sent", userName);
    return providers;
  }

  /**
   * Get the 5 closest attractions nearby the user defined by its userName with distance and reward
   * points earned when visiting them.
   *
   * @param userName of the user
   * @return list of attractions
   * @throws UserNotFoundException when user not found
   */
  @RequestMapping("/getNearbyAttractions")
  public NearbyAttractionsListDto getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
    LOGGER.info("Request: Get user {} nearby attractions", userName);
    NearbyAttractionsListDto nearbyAttractionsList = tourGuideService.getNearByAttractions(userName);
    LOGGER.info("Response: User {} nearby attractions sent", userName);
    return nearbyAttractionsList;
  }

}