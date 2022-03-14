package tourGuide.utils;

import gpsUtil.location.Attraction;
import tourGuide.dto.AttractionDto;

/**
 * Mapper utility class to map Attraction DTO and entity.
 */
public class AttractionMapper {

  private AttractionMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a Attraction entity into DTO.
   *
   * @param attraction to map
   * @return corresponding AttractionDto mapped
   */
  public static AttractionDto toDto(Attraction attraction) {
    return new AttractionDto(
        attraction.attractionId,
        attraction.longitude,
        attraction.latitude,
        attraction.attractionName,
        attraction.city,
        attraction.state
    );
  }

}