package tourGuide.dto;

/**
 * Dto Class for an Attraction with location, distance from the user, and reward points earned for
 * visiting it.
 */
public class NearbyAttractionDto {

  /**
   * Constructor for an instance of AttractionDto with name, location, distance and reward points.
   *
   * @param name of the attraction
   * @param longitude of the attraction
   * @param latitude of the attraction
   * @param distance from the user
   * @param rewardPoints earned for visiting it
   */
  public NearbyAttractionDto(String name, double latitude, double longitude, double distance, int rewardPoints) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.distance = distance;
    this.rewardPoints = rewardPoints;
  }

  private final String name;
  private final double longitude;
  private final double latitude;
  private final double distance;
  private final int rewardPoints;

  public String getName() {
    return name;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public double getDistance() {
    return distance;
  }

  public int getRewardPoints() {
    return rewardPoints;
  }
}
