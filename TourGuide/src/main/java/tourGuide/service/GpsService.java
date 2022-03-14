package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Map;
import java.util.UUID;
import org.springframework.stereotype.Service;
import tourGuide.dto.LocationDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.exception.NoLocationFoundException;

/**
 * Service Interface to retrieve users and attractions location and manage distance calculation.
 */
@Service
public interface GpsService {

  Map<Attraction, Double> getAttractionsWithDistances(Location location);

  Map<Attraction, Double> getTopNearbyAttractionsWithDistances(Location location, int top);

  /**
   * Get the last visited location registered of the user or throws an exception if no location
   * found.
   *
   * @param userId of the user
   * @return Visited location Dto
   * @throws NoLocationFoundException when no location found
   */
  VisitedLocationDto getLastLocation(UUID userId) throws NoLocationFoundException;

  /**
   * Track the current location of the user and registered it.
   *
   * @param userId of the user
   * @return current visited location Dto
   */
  VisitedLocationDto trackUserLocation(UUID userId);

  /**
   * Add to the user a new location.
   *
   * @param userId of the user
   * @param location location to add
   */
  void addLocation(UUID userId, LocationDto location);

  /**
   * Return the distance in miles between two location
   *
   * @param loc1 first location
   * @param loc2 second location
   * @return distance in miles
   */
  double getDistance(Location loc1, Location loc2);

  VisitedLocation getUserLocation(UUID userId);

}
