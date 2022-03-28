package tripservice.utils;

import java.math.BigDecimal;
import shared.dto.ProviderDto;
import tripPricer.Provider;

/**
 * Mapper utility class to map Provider DTO and entity.
 */
public class ProviderMapper {

  private ProviderMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a Provider entity into DTO.
   *
   * @param provider to map
   * @return corresponding ProviderDto mapped
   */
  public static ProviderDto toDto(Provider provider) {
    return new ProviderDto(
        provider.tripId,
        provider.name,
        BigDecimal.valueOf(provider.price)
    );
  }

}
