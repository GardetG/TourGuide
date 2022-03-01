package tourGuide.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import tourGuide.domain.UserPreferences;
import tourGuide.service.impl.TripDealsServiceImpl;
import tripPricer.Provider;
import tripPricer.TripPricer;

class TripDealsServiceTest {

  private TripPricer tripPricer;
  private TripDealsService tripDealsService;

  @BeforeEach
  void setUp() {
    tripPricer = Mockito.mock(TripPricer.class);
    tripDealsService = new TripDealsServiceImpl(tripPricer);
  }

  // Return a list of 5 providers with the following price [0,25,50,75,100] for testing purposes
  private List<Provider> createProviders(UUID attractionId) {
    return IntStream.range(0,5)
        .mapToObj(index -> new Provider(attractionId, String.format("Provider %s", index), index*25))
        .collect(Collectors.toList());
  }

  @DisplayName("Get trip deals should return providers list from tripPricer")
  @Test
  void getTripDealsTest() {
    // Given
    UUID attractionId = UUID.randomUUID();
    double lowerPricePoint = 0;
    double highPricePoint = Integer.MAX_VALUE;
    UserPreferences preferences = new UserPreferences(lowerPricePoint,highPricePoint,1, 1,2,3);
    int rewardPoint = 100;
    List<Provider> providers = createProviders(attractionId);
    when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(),anyInt(),anyInt()))
        .thenReturn(providers);

    // When
    List<Provider> actualProviders = tripDealsService.getTripDeals(attractionId, preferences, rewardPoint);

    // Then
    assertThat(actualProviders)
        .hasSize(5)
        .isEqualTo(providers);
    verify(tripPricer, times(1))
        .getPrice("test-server-api-key", attractionId, 2,3,1, 100);
  }

  @DisplayName("Get trip deals should return providers list filtered according preferences")
  @Test
  void getTripDealsFilteredTest() {
    // Given
    UUID attractionId = UUID.randomUUID();
    double lowerPricePoint = 25;
    double highPricePoint = 75;
    UserPreferences preferences = new UserPreferences(lowerPricePoint, highPricePoint,1, 1,2,3);
    int rewardPoint = 100;
    List<Provider> providers = createProviders(attractionId);
    when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(),anyInt(),anyInt()))
        .thenReturn(providers);

    // When
    List<Provider> actualProviders = tripDealsService.getTripDeals(attractionId, preferences, rewardPoint);

    // Then
    assertThat(actualProviders)
        .hasSize(3)
        .containsExactly(providers.get(1), providers.get(2), providers.get(3));
    verify(tripPricer, times(1))
        .getPrice("test-server-api-key", attractionId, 2,3,1, 100);
  }

}
