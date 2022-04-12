package rewardservice.domain;

/**
 * Location Entity with latitude and longitude.
 */
public class Location {

  public Location(double latitude, double longitude) {
    this.latitude = latitude;
    this.longitude = longitude;
  }

  public final double longitude;
  public final double latitude;

}
