package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import shared.utils.validator.RangeCheck;

/**
 * Dto class for UserPreference entity.
 */
@RangeCheck(message = "Lower price point cannot be greater than High price point")
public class PreferencesDto {

  @JsonCreator
  public PreferencesDto(@JsonProperty("lowerPricePoint") BigDecimal lowerPricePoint,
                        @JsonProperty("highPricePoint") BigDecimal highPricePoint,
                        @JsonProperty("tripDuration") int tripDuration,
                        @JsonProperty("ticketQuantity") int ticketQuantity,
                        @JsonProperty("numberOfAdults") int numberOfAdults,
                        @JsonProperty("numberOfChildren") int numberOfChildren) {
    this.lowerPricePoint = lowerPricePoint;
    this.highPricePoint = highPricePoint;
    this.tripDuration = tripDuration;
    this.ticketQuantity = ticketQuantity;
    this.numberOfAdults = numberOfAdults;
    this.numberOfChildren = numberOfChildren;
  }

  @PositiveOrZero(message = "Lower price point cannot be negative")
  private final BigDecimal lowerPricePoint;
  @PositiveOrZero(message = "High price point cannot be negative")
  private final BigDecimal highPricePoint;
  @Positive(message = "Trip Duration cannot be negative or equals to 0")
  private final int tripDuration;
  @PositiveOrZero(message = "Ticket Quantity cannot be negative")
  private final int ticketQuantity;
  @PositiveOrZero(message = "Number of Adults cannot be negative")
  private final int numberOfAdults;
  @PositiveOrZero(message = "Number of Children cannot be negative")
  private final int numberOfChildren;

  public BigDecimal getLowerPricePoint() {
    return lowerPricePoint;
  }

  public BigDecimal getHighPricePoint() {
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
