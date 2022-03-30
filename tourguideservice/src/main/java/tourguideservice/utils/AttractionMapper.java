package tourguideservice.utils;

import gpsUtil.location.Attraction;
import shared.dto.AttractionDto;

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
        attraction.attractionName,
        attraction.city,
        attraction.state,
        attraction.latitude,
        attraction.longitude
    );
  }

  /**
   * Map a Attraction Dto into entity.
   *
   * @param attractionDto to map
   * @return corresponding Attraction mapped
   */
  public static Attraction toEntity(AttractionDto attractionDto) {
    return new Attraction(
        attractionDto.getAttractionName(),
        attractionDto.getCity(),
        attractionDto.getState(),
        attractionDto.getLatitude(),
        attractionDto.getLongitude()
    );
  }

}