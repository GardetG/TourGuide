package tourguideservice.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import shared.dto.LocationDto;
import tourguideservice.dto.NearbyAttractionsListDto;
import shared.dto.UserRewardDto;
import shared.dto.VisitedLocationDto;
import shared.exception.UserNotFoundException;

/**
 * Service interface for the main service of TourGuide.
 */
@Service
public interface TourGuideService {

  /**
   * Get current location of the user defined by the provided userName.
   *
   * @param userName of the user
   * @return LocationDto
   * @throws UserNotFoundException when user not found
   */
  LocationDto getUserLocation(String userName) throws UserNotFoundException;

  /**
   * Get list of all users current location with their id.
   *
   * @return Map of id and LocationDto
   */
  Map<UUID, LocationDto> getAllCurrentLocations();

  /**
   * Get the list of all rewards of the user defined by the provided userName.
   *
   * @param userName of the user
   * @return list of UserRewardDto
   * @throws UserNotFoundException when user not found
   */
  List<UserRewardDto> getUserRewards(String userName) throws UserNotFoundException;

  /**
   * Get the preferences of the user defined by the provided userName.
   *
   * @param username of the user
   * @return UserPreferencesDto
   * @throws UserNotFoundException when user not found
   */
  PreferencesDto getUserPreferences(String username) throws UserNotFoundException;

  /**
   * Update the preference of the user defined by the provided userName according to the provided
   * values, and return the updated preferences.
   *
   * @param username of the user
   * @param userPreferences to update
   * @return updated UserPreferencesDto
   * @throws UserNotFoundException when user not found
   */
  PreferencesDto setUserPreferences(String username, PreferencesDto userPreferences) throws UserNotFoundException;

  /**
   * Get a list of trip deals providers for the user defined by the provided userName according to
   * the user preferences.
   *
   * @param userName of the user
   * @return List of ProviderDto
   * @throws UserNotFoundException when user not found
   */
  List<ProviderDto> getTripDeals(String userName) throws UserNotFoundException;

  /**
   * Get the 5 closest attractions nearby the user defined by its userName with distance and reward
   * points earned when visiting them.
   *
   * @param userName of the user
   * @return NearbyAttractionsListDto
   * @throws UserNotFoundException when user not found
   */
  NearbyAttractionsListDto getNearByAttractions(String userName) throws UserNotFoundException;

  /**
   * Track the current user location and return the current visited location Dto.
   *
   * @param userId of the user
   * @return Visited location Dto
   */
  VisitedLocationDto trackUserLocation(UUID userId);

  /**
   * Calculate and update the user rewards.
   *
   * @param userId of the user
   */
  void calculateRewards(UUID userId);

  /**
   * Get the user Id of the user corresponding to the provided userName or throw an exception if
   * user not found.
   *
   * @param userName of the user
   * @return User id
   * @throws UserNotFoundException if user not found
   */
  UUID getUserId(String userName) throws UserNotFoundException;

  /**
   * Get the list of all users' id.
   *
   * @return List of user Id
   */
  List<UUID> getAllUsersId();

}
