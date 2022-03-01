package tourGuide.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import tourGuide.domain.UserPreferences;
import tripPricer.Provider;

/**
 * Service Interface to retrieve trip deals for an user.
 */
@Service
public interface TripDealsService {

  /**
   * Get a list of providers with trips prices according to the user preferences and reward points.
   *
   * @param attractionId of the trip
   * @param preferences of the user
   * @param rewardPoints of the user
   * @return list of provider
   */
  List<Provider> getTripDeals(UUID attractionId, UserPreferences preferences, int rewardPoints);

}
