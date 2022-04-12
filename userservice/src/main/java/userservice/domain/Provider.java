package userservice.domain;

import java.util.UUID;

/**
 * Provider Entity with name and trip price.
 */
public class Provider {

  /**
   * Constructor for Provider with id of the trip and price.
   *
   * @param tripId id of the trip
   * @param name   of the provider
   * @param price  of the trip
   */
  public Provider(UUID tripId, String name, double price) {
    this.name = name;
    this.tripId = tripId;
    this.price = price;
  }

  public final String name;
  public final double price;
  public final UUID tripId;

}
