package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dto Class for a Location.
 */
public class LocationDto {

  /**
   * Constructor for an instance of LocationDto with longitude and latitude.
   *
   * @param latitude  of the location
   * @param longitude of the location
   */
  @JsonCreator
  public LocationDto(@JsonProperty("latitude") double latitude,
                     @JsonProperty("longitude") double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  private final double latitude;
  private final double longitude;

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }


}
