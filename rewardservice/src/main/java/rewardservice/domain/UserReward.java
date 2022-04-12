package rewardservice.domain;

/**
 * UserReward Entity with attraction, user location and rewards point.
 */
public class UserReward {

  /**
   * Constructor for UserReward.
   *
   * @param visitedLocation of the user
   * @param attraction      rewarded attraction
   * @param rewardPoints    reward points earned
   */
  public UserReward(VisitedLocation visitedLocation, Attraction attraction, int rewardPoints) {
    this.visitedLocation = visitedLocation;
    this.attraction = attraction;
    this.rewardPoints = rewardPoints;
  }

  public final VisitedLocation visitedLocation;
  public final Attraction attraction;
  private int rewardPoints;

  public void setRewardPoints(int rewardPoints) {
    this.rewardPoints = rewardPoints;
  }

  public int getRewardPoints() {
    return rewardPoints;
  }

}
