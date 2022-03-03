package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.domain.UserReward;
import tourGuide.dto.ProviderDto;
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

  VisitedLocation trackUserLocation(User user);

  List<Attraction> getNearByAttractions(VisitedLocation visitedLocation);

}
