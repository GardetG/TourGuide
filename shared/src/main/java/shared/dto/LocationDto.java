package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

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

  @DecimalMin(value = "-90", message = "Latitude can't be less than -90")
  @DecimalMax(value = "90", message = "Latitude can't be more than 90")
  private final double latitude;

  @DecimalMin(value = "-180", message = "Longitude can't be less than -180")
  @DecimalMax(value = "180", message = "Longitude can't be more than 180")
  private final double longitude;

  public double getLatitude() {
    return latitude;
  }

  public double getLongitude() {
    return longitude;
  }


}
