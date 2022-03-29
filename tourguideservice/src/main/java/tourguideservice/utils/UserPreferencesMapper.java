package tourguideservice.utils;

import tourguideservice.domain.UserPreferences;
import tourguideservice.dto.UserPreferencesDto;

/**
 * Mapper utility class to map UserPreferences DTO and entity.
 */
public class UserPreferencesMapper {

  private UserPreferencesMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a UserPreferences entity into DTO.
   *
   * @param userPreferences to map
   * @return corresponding UserPreferencesDto mapped
   */
  public static UserPreferencesDto toDto(UserPreferences userPreferences) {
    return new UserPreferencesDto(
        userPreferences.getLowerPricePoint().getNumber().doubleValue(),
        userPreferences.getHighPricePoint().getNumber().doubleValue(),
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
  public static UserPreferences toEntity(UserPreferencesDto userPreferencesDto) {
    return new UserPreferences(
        userPreferencesDto.getLowerPricePoint(),
        userPreferencesDto.getHighPricePoint(),
        userPreferencesDto.getTripDuration(),
        userPreferencesDto.getTicketQuantity(),
        userPreferencesDto.getNumberOfAdults(),
        userPreferencesDto.getNumberOfChildren()
    );
  }

}
