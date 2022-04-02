package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

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

  @NotNull(message = "User id is mandatory")
  private final UUID userId;
  @Valid
  @NotNull(message = "Location is mandatory")
  private final LocationDto location;
  @NotNull(message = "Time visited is mandatory")
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
