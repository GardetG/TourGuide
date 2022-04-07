package userservice.utils;

import java.math.BigDecimal;
import shared.dto.PreferencesDto;
import userservice.domain.UserPreferences;

/**
 * Mapper utility class to map UserPreferences DTO and entity.
 */
public class PreferencesMapper {

  private PreferencesMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a UserPreferences entity into DTO.
   *
   * @param userPreferences to map
   * @return corresponding UserPreferencesDto mapped
   */
  public static PreferencesDto toDto(UserPreferences userPreferences) {
    return new PreferencesDto(
        BigDecimal.valueOf(userPreferences.getLowerPricePoint().getNumber().doubleValue()),
        BigDecimal.valueOf(userPreferences.getHighPricePoint().getNumber().doubleValue()),
        userPreferences.getTripDuration(),
        userPreferences.getTicketQuantity(),
        userPreferences.getNumberOfAdults(),
        userPreferences.getNumberOfChildren()
    );
  }

  /**
   * Map a UserPreferences Dto into entity.
   *
   * @param userPreferencesDto to map
   * @return corresponding UserPreferences mapped
   */
  public static UserPreferences toEntity(PreferencesDto userPreferencesDto) {
    return new UserPreferences(
        userPreferencesDto.getLowerPricePoint().doubleValue(),
        userPreferencesDto.getHighPricePoint().doubleValue(),
        userPreferencesDto.getTripDuration(),
        userPreferencesDto.getTicketQuantity(),
        userPreferencesDto.getNumberOfAdults(),
        userPreferencesDto.getNumberOfChildren()
    );
  }

}
