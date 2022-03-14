package tourGuide.utils;

import gpsUtil.location.VisitedLocation;
import tourGuide.dto.VisitedLocationDto;

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

}