package tourguideservice.utils;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import shared.dto.ProviderDto;
import tourguideservice.domain.Provider;

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

  /**
   * Map a Provider DTO into entity.
   *
   * @param providerDto to map
   * @return corresponding Provider mapped
   */
  public static Provider toEntity(ProviderDto providerDto) {
    return new Provider(
        providerDto.getTripId(),
        providerDto.getName(),
        providerDto.getPrice().doubleValue()
    );
  }

  /**
   * Map a list of Provide Dto into Entity.
   *
   * @param providers to map
   * @return corresponding list of Providers mapped
   */
  public static List<Provider> toEntity(List<ProviderDto> providers) {
    return providers.stream()
        .map(ProviderMapper::toEntity)
        .collect(Collectors.toList());
  }
}
