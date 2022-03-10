package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.domain.UserReward;
import tourGuide.dto.LocationDto;
import tourGuide.dto.NearbyAttractionsListDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.dto.UserRewardDto;
import tourGuide.exception.UserNotFoundException;

/**
 * Service interface for the main service of TourGuide.
 */
@Service
public interface TourGuideService {

  List<UserRewardDto> getUserRewards(String userName) throws UserNotFoundException;

  LocationDto getUserLocation(String userName) throws UserNotFoundException;

  User getUser(String userName) throws UserNotFoundException;

  List<User> getAllUsers();

  Map<UUID, LocationDto> getAllCurrentLocations();

  List<ProviderDto> getTripDeals(String userName) throws UserNotFoundException;

  UserPreferencesDto getUserPreferences(String username) throws UserNotFoundException;

  UserPreferencesDto setUserPreferences(String username, UserPreferencesDto userPreferences) throws UserNotFoundException;

  VisitedLocation trackUserLocation(User user);

  NearbyAttractionsListDto getNearByAttractions(String userName) throws UserNotFoundException;

}
