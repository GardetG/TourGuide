package tourGuide.dto;

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
   * @param name of the provider
   * @param price of the trip
   */
  public ProviderDto(UUID tripId, String name, double price) {
    this.name = name;
    this.tripId = tripId;
    this.price = price;
  }

  private final String name;
  private final double price;
  private final UUID tripId;

  public String getName() {
    return name;
  }

  public double getPrice() {
    return price;
  }

  public UUID getTripId() {
    return tripId;
  }

}
