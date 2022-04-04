package rewardservice.utils;

import rewardservice.domain.Location;
import rewardservice.domain.VisitedLocation;
import shared.dto.LocationDto;
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
        new LocationDto(
            visitedLocation.location.latitude,
            visitedLocation.location.longitude
        ),
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
        new Location(
            visitedLocationDto.getLocation().getLatitude(),
            visitedLocationDto.getLocation().getLongitude()
        ),
        visitedLocationDto.getTimeVisited()
    );
  }

}