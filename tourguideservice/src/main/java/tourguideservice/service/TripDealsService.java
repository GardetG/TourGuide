package tourguideservice.service;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;

/**
 * Service Interface to retrieve trip deals for a user.
 */
@Service
public interface TripDealsService {

  /**
   * Get a list of providers with trip prices according to the user preferences and reward points.
   *
   * @param attractionId of the trip
   * @param preferences of the user
   * @param rewardPoints of the user
   * @return list of provider
   */
  List<ProviderDto> getTripDeals(UUID attractionId, PreferencesDto preferences, int rewardPoints);

}
