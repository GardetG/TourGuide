package rewardservice.domain;

import java.util.UUID;

/**
 * Attraction Entity with name and location.
 */
public class Attraction extends Location {

  /**
   * Constructor to instantiate an Attraction with name, city, state and location.
   *
   * @param attractionName of the Attraction
   * @param city           of the Attraction
   * @param state          of the Attraction
   * @param latitude       of the Attraction
   * @param longitude      of the Attraction
   */
  public Attraction(String attractionName, String city, String state, double latitude,
                    double longitude) {
    super(latitude, longitude);
    this.attractionName = attractionName;
    this.city = city;
    this.state = state;
    this.attractionId = UUID.randomUUID();
  }

  public final String attractionName;
  public final String city;
  public final String state;
  public final UUID attractionId;

}