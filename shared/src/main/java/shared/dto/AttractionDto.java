package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

/**
 * Dto Class for an Attraction.
 */
public class AttractionDto {

  /**
   * Constructor for an instance of AttractionDto with longitude and latitude, id and name.
   *
   * @param attractionId   of the attraction
   * @param attractionName of the attraction
   * @param city           of the attraction
   * @param state          of the attraction
   * @param latitude       of the attraction location
   * @param longitude      of the attraction location
   */
  @JsonCreator
  public AttractionDto(@JsonProperty("attractionId") UUID attractionId,
                       @JsonProperty("attractionName") String attractionName,
                       @JsonProperty("city") String city,
                       @JsonProperty("state") String state,
                       @JsonProperty("latitude") double latitude,
                       @JsonProperty("longitude") double longitude) {
    this.attractionId = attractionId;
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
    this.latitude = latitude;
    this.longitude = longitude;
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
