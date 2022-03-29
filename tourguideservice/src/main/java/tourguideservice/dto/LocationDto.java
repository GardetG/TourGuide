package tourguideservice.dto;

/**
 * Dto Class for a Location.
 */
public class LocationDto {

  /**
   * Constructor for an instance of LocationDto with longitude and latitude.
   *
   * @param longitude of the location
   * @param latitude of the location
   */
  public LocationDto(double longitude, double latitude) {
    this.longitude = longitude;
    this.latitude = latitude;
  }

  private final double longitude;
  private final double latitude;

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

}
