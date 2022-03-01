package tourGuide.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.money.CurrencyUnit;
import javax.money.Monetary;
import org.javamoney.moneta.Money;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.domain.UserPreferences;
import tourGuide.service.TripDealsService;
import tripPricer.Provider;
import tripPricer.TripPricer;

/**
 * Service implementation Class to retrieve trip deals for a user.
 */
@Service
public class TripDealsServiceImpl implements TripDealsService {

  private final TripPricer tripPricer;

  @Autowired
  public TripDealsServiceImpl(TripPricer tripPricer) {
    this.tripPricer = tripPricer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<Provider> getTripDeals(UUID attractionId, UserPreferences preferences,
                                     int rewardPoints) {
    List<Provider> providers = tripPricer.getPrice(
        "test-server-api-key",
        attractionId,
        preferences.getNumberOfAdults(),
        preferences.getNumberOfChildren(),
        preferences.getTripDuration(),
        rewardPoints
    );
    return filterByPrice(providers,
        preferences.getLowerPricePoint(),
        preferences.getHighPricePoint()
    );
  }

  /**
   * Filtered a list of provider by price according to the lower and high price points defined in
   * user preferences.
   *
   * @param providers list to filtered
   * @param lowerPricePoint of the trip price
   * @param highPricePoint of the trip price
   * @return filtered list of providers
   */
  private List<Provider> filterByPrice(List<Provider> providers, Money lowerPricePoint,
                                       Money highPricePoint) {
    return providers.stream()
        .filter(provider -> isInRange(provider.price, lowerPricePoint, highPricePoint))
        .collect(Collectors.toList());
  }

  /**
   * Check if a price is in the range define by the lower and high price points.
   *
   * @param price of the trip
   * @param lowerPricePoint of the trip price
   * @param highPricePoint of the trip price
   * @return true if in range and else otherwise
   */
  private boolean isInRange(double price, Money lowerPricePoint, Money highPricePoint) {
    CurrencyUnit currency = Monetary.getCurrency("USD");
    Money tripPrice = Money.of(price, currency);
    boolean isGreaterThanLowerPricePoint = tripPrice.isGreaterThanOrEqualTo(lowerPricePoint);
    boolean isLessThanHighPricePoint = tripPrice.isLessThanOrEqualTo(highPricePoint);
    return isGreaterThanLowerPricePoint && isLessThanHighPricePoint;
  }

}
