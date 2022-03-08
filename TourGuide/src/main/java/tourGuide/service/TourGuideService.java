package tourGuide.service;

import gpsUtil.location.VisitedLocation;
import java.util.List;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.domain.UserReward;
import tourGuide.dto.NearbyAttractionsDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.exception.UserNotFoundException;

/**
 * Service interface for the main service of TourGuide.
 */
@Service
public interface TourGuideService {

  List<UserReward> getUserRewards(User user);

  VisitedLocation getUserLocation(User user);

  User getUser(String userName) throws UserNotFoundException;

  List<User> getAllUsers();

  List<ProviderDto> getTripDeals(String userName) throws UserNotFoundException;

  UserPreferencesDto getUserPreferences(String username) throws UserNotFoundException;

  UserPreferencesDto setUserPreferences(String username, UserPreferencesDto userPreferences) throws UserNotFoundException;

  VisitedLocation trackUserLocation(User user);

  NearbyAttractionsDto getNearByAttractions(String userName) throws UserNotFoundException;

}
