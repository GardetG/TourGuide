package rewardservice.domain;

import java.util.Date;
import java.util.UUID;

/**
 * VisitedLocation Entity with user id, location and time.
 */
public class VisitedLocation {

  /**
   * Constrcutor of VisitedLocation with user Id, location and time of the visit.
   *
   * @param userId      id of the user
   * @param location    of the user
   * @param timeVisited time of the visit
   */
  public VisitedLocation(UUID userId, Location location, Date timeVisited) {
    this.userId = userId;
    this.location = location;
    this.timeVisited = timeVisited;
  }

  public final UUID userId;
  public final Location location;
  public final Date timeVisited;

}
