package locationservice.service;

import gpsUtil.location.Location;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import shared.dto.AttractionDto;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

/**
 * Service Interface to retrieve users and attractions location and manage distance calculation.
 */
@Service
public interface GpsService {

  /**
   * Get the last visited location registered of the user or throws an exception if no location
   * found.
   *
   * @param userId of the user
   * @return Visited location Dto
   * @throws NoLocationFoundException when no location found
   */
  VisitedLocationDto getUserLastVisitedLocation(UUID userId) throws NoLocationFoundException;

  /**
   * Get the last visited location registered of each user.
   *
   * @return List of visited location Dto
   */
  List<VisitedLocationDto> getAllUserLastVisitedLocation();

  /**
   * Track the current location of the user and registered it.
   *
   * @param userId of the user
   * @return current visited location Dto
   */
  VisitedLocationDto trackUserLocation(UUID userId);

  /**
   * Add to the user a list of new visited locations.
   * @param userId of the user
   * @param visitedLocationDtos to add
   */
  void addVisitedLocation(UUID userId, List<VisitedLocationDto> visitedLocationDtos);

  /**
   * Get the list of attractions.
   *
   * @return list of attraction Dto
   */
  List<AttractionDto> getAttractions();

  /**
   * Return a list of the visited attractions of the user and the corresponding visited location
   * that was in range of the attraction.
   *
   * @param userId of the user
   * @return List of attractions and corresponding visited location Dto
   */
  List<VisitedAttractionDto> getVisitedAttractions(UUID userId);

  /**
   * Return a list of the nearest attractions and their distance from the user, sorted from the
   * closest to the farthest. The limit truncate the list to keep only the nearest attractions.
   * The limit can't be negative or would throw an IllegalArgumentException.
   *
   * @param userId of the user
   * @param limit number of records
   * @return List of attraction with distance Dto
   * @throws NoLocationFoundException when no user location found
   * @throws IllegalArgumentException when limit is negative
   */
  List<AttractionWithDistanceDto> getNearbyAttractions(UUID userId, int limit)
      throws NoLocationFoundException;

  /**
   * Return the distance in miles between two location
   *
   * @param loc1 first location
   * @param loc2 second location
   * @return distance in miles
   */
  double getDistance(Location loc1, Location loc2);

}
