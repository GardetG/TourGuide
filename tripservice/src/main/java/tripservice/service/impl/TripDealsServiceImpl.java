package tripservice.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shared.dto.ProviderDto;
import shared.dto.PreferencesDto;
import tripPricer.Provider;
import tripPricer.TripPricer;
import tripservice.config.TripServiceProperties;
import tripservice.service.TripDealsService;
import tripservice.utils.ProviderMapper;

/**
 * Service implementation Class to retrieve trip deals for a user.
 */
@Service
public class TripDealsServiceImpl implements TripDealsService {

  private final TripPricer tripPricer;
  private final String tripPricerApiKey;

  @Autowired
  public TripDealsServiceImpl(TripPricer tripPricer, TripServiceProperties properties) {
    this.tripPricer = tripPricer;
    this.tripPricerApiKey = properties.getTripPricerApiKey();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<ProviderDto> getTripDeals(UUID attractionId, PreferencesDto preferences,
                                        int rewardPoints) {
    return retrieveTripDeals(attractionId, preferences, rewardPoints)
        .stream()
        .filter(provider -> isInRange(
            provider,
            preferences.getLowerPricePoint(),
            preferences.getHighPricePoint()
        ))
        .map(ProviderMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * Get a list of providers for an attraction from tripPricer according to the number of adults
   * and children, the trip duration and user reward points.
   *
   * @param attractionId of the attraction
   * @param preferences of the user
   * @param rewardPoints of the yser
   * @return List of providers
   */
  private List<Provider> retrieveTripDeals(UUID attractionId, PreferencesDto preferences,
                                           int rewardPoints) {
    return tripPricer.getPrice(
        tripPricerApiKey,
        attractionId,
        preferences.getNumberOfAdults(),
        preferences.getNumberOfChildren(),
        preferences.getTripDuration(),
        rewardPoints
    );
  }

  /**
   * Check if a provider price is in the range define by the lower and high price points.
   *
   * @param provider of the trip
   * @param lowerPricePoint of the trip price
   * @param highPricePoint of the trip price
   * @return true if in range and false otherwise
   */
  private boolean isInRange(Provider provider, BigDecimal lowerPricePoint, BigDecimal highPricePoint) {
    BigDecimal tripPrice = BigDecimal.valueOf(provider.price);
    boolean isGreaterThanLowerPricePoint = tripPrice.compareTo(lowerPricePoint) >= 0;
    boolean isLessThanHighPricePoint = tripPrice.compareTo(highPricePoint) <=0 ;
    return isGreaterThanLowerPricePoint && isLessThanHighPricePoint;
  }

}
