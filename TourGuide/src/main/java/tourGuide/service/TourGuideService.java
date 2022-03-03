package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import org.springframework.stereotype.Service;
import tourGuide.domain.User;
import tourGuide.domain.UserReward;
import tripPricer.Provider;

/**
 * Service interface for the main service of TourGuide.
 */
@Service
public interface TourGuideService {

  List<UserReward> getUserRewards(User user);

  VisitedLocation getUserLocation(User user);

  User getUser(String userName);

  List<User> getAllUsers();

  List<Provider> getTripDeals(User user);

  VisitedLocation trackUserLocation(User user);

  List<Attraction> getNearByAttractions(VisitedLocation visitedLocation);

}
