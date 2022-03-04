package tourGuide.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import tourGuide.utils.validator.RangeCheck;

/**
 * Dto class for UserPreference entity.
 */
@RangeCheck(message = "Lower price point cannot be greater than High price point")
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

  @PositiveOrZero(message = "Lower price point cannot be negative")
  private final double lowerPricePoint;
  @PositiveOrZero(message = "High price point cannot be negative")
  private final double highPricePoint;
  @Positive(message = "Trip Duration cannot be negative or equals to 0")
  private final int tripDuration;
  @PositiveOrZero(message = "Ticket Quantity cannot be negative")
  private final int ticketQuantity;
  @PositiveOrZero(message = "Number of Adults cannot be negative")
  private final int numberOfAdults;
  @PositiveOrZero(message = "Number of Children cannot be negative")
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
