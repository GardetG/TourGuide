package tourGuide.service;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionsListDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.exception.UserNotFoundException;

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
  UserPreferencesDto getUserPreferences(String username) throws UserNotFoundException;

  /**
   * Update the preference of the user defined by the provided userName according to the provided
   * values, and return the updated preferences.
   *
   * @param username of the user
   * @param userPreferences to update
   * @return updated UserPreferencesDto
   * @throws UserNotFoundException when user not found
   */
  UserPreferencesDto setUserPreferences(String username, UserPreferencesDto userPreferences) throws UserNotFoundException;

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

  User getUser(String userName) throws UserNotFoundException;

  List<User> getAllUsers();

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

}
