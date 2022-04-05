package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Dto Class for a user reward.
 */
public class UserRewardDto {

  /**
   * Constructor for an instance of UserRewardDto with the location of the user, the attraction and
   * the number of reward points earned.
   *
   * @param visitedLocation linked to the reward
   * @param attraction      linked to the reward
   * @param rewardPoints    earned
   */
  @JsonCreator
  public UserRewardDto(@JsonProperty("visitedLocation") VisitedLocationDto visitedLocation,
                       @JsonProperty("attraction") AttractionDto attraction,
                       @JsonProperty("rewardPoints") int rewardPoints) {
    this.visitedLocation = visitedLocation;
    this.attraction = attraction;
    this.rewardPoints = rewardPoints;
  }

  private final VisitedLocationDto visitedLocation;
  private final AttractionDto attraction;
  private final int rewardPoints;

  public VisitedLocationDto getVisitedLocation() {
    return visitedLocation;
  }

  public AttractionDto getAttraction() {
    return attraction;
  }

  public int getRewardPoints() {
    return rewardPoints;
  }

}
