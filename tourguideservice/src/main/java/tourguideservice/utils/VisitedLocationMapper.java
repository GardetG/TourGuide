package tourguideservice.utils;

import gpsUtil.location.VisitedLocation;
import shared.dto.VisitedLocationDto;

/**
 * Mapper utility class to map VisitedLocation DTO and entity.
 */
public class VisitedLocationMapper {

  private VisitedLocationMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a VisitedLocation entity into DTO.
   *
   * @param visitedLocation to map
   * @return corresponding VisitedLocationDto mapped
   */
  public static VisitedLocationDto toDto(VisitedLocation visitedLocation) {
    return new VisitedLocationDto(
        visitedLocation.userId,
        LocationMapper.toDto(visitedLocation.location),
        visitedLocation.timeVisited
    );
  }

  /**
   * Map a VisitedLocation Dto into entity.
   *
   * @param visitedLocationDto to map
   * @return corresponding VisitedLocation mapped
   */
  public static VisitedLocation toEntity(VisitedLocationDto visitedLocationDto) {
    return new VisitedLocation(
        visitedLocationDto.getUserId(),
        LocationMapper.toEntity(visitedLocationDto.getLocation()),
        visitedLocationDto.getTimeVisited()
    );
  }

}