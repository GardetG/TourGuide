package tourGuide.dto;

import java.util.List;

/**
 * Dto class for retrieving the closest attractions from a user, with user location, and the list of
 * attraction with name, location, distance and reward points caned for visiting it.
 */
public class NearbyAttractionsDto {

  /**
   * Constructor for an instance of NearbyAttractionDto with user location and list of
   * attractionsDto.
   *
   * @param userLocation location of the user
   * @param attractions list of attractions
   */
  public NearbyAttractionsDto(LocationDto userLocation,
                              List<AttractionDto> attractions) {
    this.userLocation = userLocation;
    this.attractions = attractions;
  }

  private final LocationDto userLocation;
  private final List<AttractionDto> attractions;

  public LocationDto getUserLocation() {
    return userLocation;
  }

  public List<AttractionDto> getAttractions() {
    return attractions;
  }
}
