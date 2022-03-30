package locationservice.utils;

import gpsUtil.location.Location;
import shared.dto.LocationDto;

/**
 * Mapper utility class to map Location DTO and entity.
 */
public class LocationMapper {

  private LocationMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map a Location entity into DTO.
   *
   * @param location to map
   * @return corresponding LocationDto mapped
   */
  public static LocationDto toDto(Location location) {
    return new LocationDto(
        location.latitude,
        location.longitude
    );
  }

  /**
   * Map a Location Dto into entity.
   *
   * @param locationDto to map
   * @return corresponding Location mapped
   */
  public static Location toEntity(LocationDto locationDto) {
    return new Location(
        locationDto.getLatitude(),
        locationDto.getLongitude()
    );
  }

}