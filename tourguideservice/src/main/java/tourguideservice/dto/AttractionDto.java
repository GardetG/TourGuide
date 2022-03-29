package tourguideservice.dto;

import java.util.UUID;

/**
 * Dto Class for an Attraction.
 */
public class AttractionDto {

  /**
   * Constructor for an instance of AttractionDto with longitude and latitude, id and name.
   *
   * @param attractionId of the attraction
   * @param longitude of the attraction location
   * @param latitude of the attraction location
   * @param attractionName of the attraction
   * @param city of the attraction
   * @param state of the attraction
   */
  public AttractionDto(UUID attractionId, double longitude, double latitude,
                       String attractionName, String city, String state) {
    this.attractionId = attractionId;
    this.longitude = longitude;
    this.latitude = latitude;
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
  }

  private final UUID attractionId;
  private final double longitude;
  private final double latitude;
  private final String attractionName;
  private final String city;
  private final String state;

  public UUID getAttractionId() {
    return attractionId;
  }

  public double getLongitude() {
    return longitude;
  }

  public double getLatitude() {
    return latitude;
  }

  public String getAttractionName() {
    return attractionName;
  }

  public String getCity() {
    return city;
  }

  public String getState() {
    return state;
  }

}
