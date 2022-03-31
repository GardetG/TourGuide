package locationservice.controller;

import java.util.UUID;
import javax.validation.Valid;
import locationservice.service.GpsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

/**
 * Controller Class exposing LocationService API end points.
 */
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
  @GetMapping("/getLastLocation")
  public VisitedLocationDto getLastLocation(@RequestParam UUID userId) throws NoLocationFoundException {
    LOGGER.info("Request: Get user {} last location", userId);
    VisitedLocationDto visitedLocation = gpsService.getLastLocation(userId);
    LOGGER.info("Response: User {} last location sent", userId);
    return visitedLocation;
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
   * Add to user a new location.
   *
   * @param visitedLocationDto visited location to add
   */
  @PostMapping("/addLocation")
  void addLocation(@Valid @RequestBody VisitedLocationDto visitedLocationDto){
    LOGGER.info("Request: Add user {} new location", visitedLocationDto.getUserId());
    gpsService.addLocation(visitedLocationDto);
    LOGGER.info("Response: User {} new location added", visitedLocationDto.getUserId());
  }


}
