package locationservice.controller;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import locationservice.service.GpsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.dto.AttractionDto;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

/**
 * Controller Class exposing LocationService API end points.
 */
@Validated
@RestController
public class LocationController {

  private static final Logger LOGGER = LoggerFactory.getLogger(LocationController.class);

  @Autowired
  GpsService gpsService;

  /**
   * Get the last visited location registered of the user or throws an exception if no location
   * found.
   *
   * @param userId of the user
   * @return HTTP 200 with last visited location Dto
   * @throws NoLocationFoundException when no location found
   */
  @GetMapping("/getUserLastVisitedLocation")
  public VisitedLocationDto getUserLastVisitedLocation(@RequestParam UUID userId)
      throws NoLocationFoundException {
    LOGGER.info("Request: Get user {} last location", userId);
    VisitedLocationDto visitedLocation = gpsService.getUserLastVisitedLocation(userId);
    LOGGER.info("Response: User {} last location sent", userId);
    return visitedLocation;
  }

  /**
   * Get the last visited location registered for each user.
   *
   * @return HTTP 200 with list of last visited location Dto
   */
  @GetMapping("/getAllUserLastVisitedLocation")
  public List<VisitedLocationDto> getAllUserLastVisitedLocation() {
    LOGGER.info("Request: Get all users' last location");
    List<VisitedLocationDto> visitedLocations = gpsService.getAllUserLastVisitedLocation();
    LOGGER.info("Response: All users' last location sent");
    return visitedLocations;
  }

  /**
   * Track the current location of the user and registered it.
   *
   * @param userId of user
   * @return HTTP 200 with current visited location Dto
   */
  @GetMapping("/trackUserLocation")
  public VisitedLocationDto trackUserLocation(@RequestParam UUID userId) {
    LOGGER.info("Request: Track user {} location", userId);
    VisitedLocationDto visitedLocation = gpsService.trackUserLocation(userId);
    LOGGER.info("Response: User {} tracked location sent", userId);
    return visitedLocation;
  }

  /**
   * Get the list of attractions.
   *
   * @return HTTP 200 with list of attraction Dto
   */
  @GetMapping("/getAttractions")
  public List<AttractionDto> getAttractions() {
    LOGGER.info("Request: Get list of all attractions");
    List<AttractionDto> attraction = gpsService.getAttractions();
    LOGGER.info("Response: List of all attractions sent");
    return attraction;
  }

  /**
   * Add to user a new location.
   *
   * @param userId id of the user
   * @param visitedLocationDto visited location to add
   */
  @PostMapping("/addVisitedLocation")
  public void addVisitedLocation(@RequestParam UUID userId,
                                 @RequestBody List<@Valid VisitedLocationDto> visitedLocationDto) {
    LOGGER.info("Request: Add locations to user {}", userId);
    gpsService.addVisitedLocation(userId, visitedLocationDto);
    LOGGER.info("Response: Location added to user {}", userId);
  }

  /**
   * Return a list of the visited attractions of the user and the corresponding visited location
   * that was in range of the attraction.
   *
   * @param userId of the user
   * @return HTTP 200 with List of attractions and corresponding visited location Dto
   */
  @GetMapping("/getVisitedAttractions")
  public List<VisitedAttractionDto> getVisitedAttractions(@RequestParam UUID userId) {
    LOGGER.info("Request: Get user {} visited attractions", userId);
    List<VisitedAttractionDto> visitedAttractions = gpsService.getVisitedAttractions(userId);
    LOGGER.info("Response: User {} visited attractions sent", userId);
    return visitedAttractions;
  }

  /**
   * Return a list of the nearest attractions and their distance from the user, sorted from the
   * closest to the farthest. The limit truncate the list to keep only the nearest attractions.
   * The limit can't be negative or would throw an IllegalArgumentException.
   *
   * @param userId of the user
   * @param limit  number of records
   * @return HTTP 200 with List of attraction with distance Dto
   * @throws NoLocationFoundException when no user location found
   * @throws IllegalArgumentException when limit is negative
   */
  @GetMapping("/getNearbyAttractions")
  List<AttractionWithDistanceDto> getNearbyAttractions(@RequestParam UUID userId,
                                                       @RequestParam int limit)
      throws NoLocationFoundException {
    LOGGER.info("Request: Get {} nearest attractions from user {}", limit, userId);
    List<AttractionWithDistanceDto> attractionsWithDistance =
        gpsService.getNearbyAttractions(userId, limit);
    LOGGER.info("Response: {} nearest attractions from user {} sent", limit, userId);
    return attractionsWithDistance;
  }

}
