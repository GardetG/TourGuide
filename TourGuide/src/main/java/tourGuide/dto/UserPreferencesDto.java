package tourGuide.dto;

/**
 * Dto class for UserPreference entity.
 */
public class UserPreferencesDto {

  /**
   * Constructor for an instance of UserPreferenceDto with price range, trip duration and number of
   * adults and children.
   *
   * @param tripDuration duration of the trip
   * @param numberOfAdults number of adults
   * @param numberOfChildren number of children
   */
  public UserPreferencesDto(double lowerPricePoint, double highPricePoint, int tripDuration,
                         int ticketQuantity, int numberOfAdults, int numberOfChildren) {
    this.lowerPricePoint = lowerPricePoint;
    this.highPricePoint = highPricePoint;
    this.tripDuration = tripDuration;
    this.ticketQuantity = ticketQuantity;
    this.numberOfAdults = numberOfAdults;
    this.numberOfChildren = numberOfChildren;
  }

  private final double lowerPricePoint;
  private final double highPricePoint;
  private final int tripDuration;
  private final int ticketQuantity;
  private final int numberOfAdults;
  private final int numberOfChildren;

  public double getLowerPricePoint() {
    return lowerPricePoint;
  }

  public double getHighPricePoint() {
    return highPricePoint;
  }

  public int getTripDuration() {
    return tripDuration;
  }

  public int getTicketQuantity() {
    return ticketQuantity;
  }

  public int getNumberOfAdults() {
    return numberOfAdults;
  }

  public int getNumberOfChildren() {
    return numberOfChildren;
  }
}
