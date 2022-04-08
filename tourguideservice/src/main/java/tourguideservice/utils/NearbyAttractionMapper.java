package tourguideservice.utils;

import shared.dto.AttractionWithDistanceDto;
import tourguideservice.dto.NearbyAttractionDto;

public class NearbyAttractionMapper {

  private NearbyAttractionMapper() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Map an attraction with distance into NearbyAttractionDto.
   *
   * @param attractionWithDistance to map
   * @param rewardPoint reward of the attraction
   * @return corresponding NearbyAttractionDto mapped
   */
  public static NearbyAttractionDto toDto(AttractionWithDistanceDto attractionWithDistance, int rewardPoint) {
    return new NearbyAttractionDto(
        attractionWithDistance.getAttraction().getAttractionName(),
        attractionWithDistance.getAttraction().getLatitude(),
        attractionWithDistance.getAttraction().getLongitude(),
        attractionWithDistance.getDistance(),
        rewardPoint
    );
  }

}
