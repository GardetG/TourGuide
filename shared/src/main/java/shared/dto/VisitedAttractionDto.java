package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dto Class for a visited attraction.
 */
public class VisitedAttractionDto {

  /**
   * Constructor for an instance of VisitedAttractionDto with the visited attraction and the visited
   * location of the user when visited it.
   *
   * @param attraction      visited by the user
   * @param visitedLocation of the user
   */
  @JsonCreator
  public VisitedAttractionDto(@JsonProperty("attraction") AttractionDto attraction,
                              @JsonProperty("visitedLocation") VisitedLocationDto visitedLocation) {
    this.attraction = attraction;
    this.visitedLocation = visitedLocation;
  }

  private final AttractionDto attraction;
  private final VisitedLocationDto visitedLocation;

  public AttractionDto getAttraction() {
    return attraction;
  }

  public VisitedLocationDto getVisitedLocation() {
    return visitedLocation;
  }

}
