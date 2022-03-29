package tripservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import shared.dto.ProviderDto;
import shared.dto.PreferencesDto;
import tripPricer.Provider;
import tripPricer.TripPricer;
import tripservice.testutils.ProviderFactory;

@SpringBootTest
class TripDealsServiceTest {

  @Autowired
  private TripDealsService tripDealsService;

  @MockBean
  private TripPricer tripPricer;

  @DisplayName("Get trip deals should return providers list according to preferences")
  @Test
  void getUserTripDealsTest() {
    // Given
    UUID attractionId = UUID.randomUUID();
    BigDecimal lowerPricePoint = BigDecimal.valueOf(0);
    BigDecimal highPricePoint = BigDecimal.valueOf(Integer.MAX_VALUE);
    PreferencesDto preferences = new PreferencesDto(lowerPricePoint,highPricePoint,1, 1,2,3);
    int rewardPoint = 100;
    List<Provider> providers = ProviderFactory.getProviders(attractionId);
    when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(),anyInt(),anyInt()))
        .thenReturn(providers);

    // When
    List<ProviderDto> actualProviders = tripDealsService.getUserTripDeals(attractionId, preferences, rewardPoint);

    // Then
    List<ProviderDto> expectedProviders = ProviderFactory.getProvidersDto(attractionId);
    assertThat(actualProviders)
        .hasSize(5)
        .usingRecursiveComparison()
        .isEqualTo(expectedProviders);
    verify(tripPricer, times(1))
        .getPrice("test-server-api-key", attractionId, 2,3,1, 100);
  }

  @DisplayName("Get trip deals should only return providers whose price is in the chosen range")
  @Test
  void getUserTripDealsFilteredTest() {
    // Given
    UUID attractionId = UUID.randomUUID();
    BigDecimal lowerPricePoint = BigDecimal.valueOf(25);
    BigDecimal highPricePoint = BigDecimal.valueOf(75);
    PreferencesDto preferences = new PreferencesDto(lowerPricePoint, highPricePoint,1, 1,2,3);
    int rewardPoint = 100;
    List<Provider> providers = ProviderFactory.getProviders(attractionId);
    when(tripPricer.getPrice(anyString(), any(UUID.class), anyInt(), anyInt(),anyInt(),anyInt()))
        .thenReturn(providers);

    // When
    List<ProviderDto> actualProviders = tripDealsService.getUserTripDeals(attractionId, preferences, rewardPoint);

    // Then
    List<ProviderDto> expectedProviders = ProviderFactory.getProvidersDto(attractionId);
    assertThat(actualProviders)
        .hasSize(3)
        .usingRecursiveFieldByFieldElementComparator()
        .containsExactly(expectedProviders.get(1), expectedProviders.get(2), expectedProviders.get(3));
    verify(tripPricer, times(1))
        .getPrice("test-server-api-key", attractionId, 2,3,1, 100);
  }

}
