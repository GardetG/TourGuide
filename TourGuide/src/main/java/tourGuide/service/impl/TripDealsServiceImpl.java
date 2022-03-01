package tourGuide.service.impl;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import tourGuide.domain.UserPreferences;
import tourGuide.service.TripDealsService;
import tripPricer.Provider;

/**
 * Service implementation Class to retrieve trip deals for an user.
 */
@Service
public class TripDealsServiceImpl implements TripDealsService {

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Provider> getTripDeals(UUID attractionId, UserPreferences preferences,
                                     int rewardPoints) {
    return null;
  }
}
