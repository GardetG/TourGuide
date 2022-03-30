package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.UUID;

/**
 * Dto Class for a VisitedLocation.
 */
public class VisitedLocationDto {

  /**
   * Constructor for an instance of VisitedLocationDto with user id, location and date of the visit.
   *
   * @param userId      of the user
   * @param location    of the user
   * @param timeVisited date of the visit
   */
  @JsonCreator
  public VisitedLocationDto(@JsonProperty("userId") UUID userId,
                            @JsonProperty("location") LocationDto location,
                            @JsonProperty("timeVisited") Date timeVisited) {
    this.userId = userId;
    this.location = location;
    this.timeVisited = timeVisited;
  }

  private final UUID userId;
  private final LocationDto location;
  private final Date timeVisited;

  public UUID getUserId() {
    return userId;
  }

  public LocationDto getLocation() {
    return location;
  }

  public Date getTimeVisited() {
    return timeVisited;
  }

}
