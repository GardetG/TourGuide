package tourGuide.utils;

import java.util.List;
import java.util.stream.Collectors;
import tourGuide.dto.ProviderDto;
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
        provider.price
    );
  }

  /**
   * Map a list of Provider entity into DTOs.
   *
   * @param providers to map
   * @return corresponding list of ProviderDto mapped
   */
  public static List<ProviderDto> toDto(List<Provider> providers) {
    return providers.stream()
        .map(ProviderMapper::toDto)
        .collect(Collectors.toList());
  }

}
