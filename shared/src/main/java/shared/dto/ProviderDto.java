package shared.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.UUID;

/**
 * Dto class for Provider entity.
 */
public class ProviderDto {

  /**
   * Constructor for an instance of ProviderDto with trip's id, name of the provider and price of
   * the trip.
   *
   * @param tripId id of the trip
   * @param name   of the provider
   * @param price  of the trip
   */
  @JsonCreator
  public ProviderDto(@JsonProperty("tripId") UUID tripId,
                     @JsonProperty("name") String name,
                     @JsonProperty("price") BigDecimal price) {
    this.name = name;
    this.tripId = tripId;
    this.price = price;
  }

  private final String name;
  private final BigDecimal price;
  private final UUID tripId;

  public String getName() {
    return name;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public UUID getTripId() {
    return tripId;
  }

}
