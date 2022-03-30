package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dto Class for an Attraction with the distance from the user.
 */
public class AttractionWithDistanceDto {

  /**
   * Constructor for an instance of AttractionWithDistanceDto with the attraction and its distance
   * from the user.
   *
   * @param attraction attraction
   * @param distance   from the user
   */
  @JsonCreator
  public AttractionWithDistanceDto(@JsonProperty("attraction") AttractionDto attraction,
                                   @JsonProperty("distance") double distance) {
    this.attraction = attraction;
    this.distance = distance;
  }

  private final AttractionDto attraction;
  private final double distance;

  public AttractionDto getAttraction() {
    return attraction;
  }

  public double getDistance() {
    return distance;
  }
}
