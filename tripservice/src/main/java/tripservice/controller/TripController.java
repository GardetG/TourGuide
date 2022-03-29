package tripservice.controller;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import shared.dto.ProviderDto;
import shared.dto.PreferencesDto;
import tripservice.service.TripDealsService;

/**
 * Controller Class exposing TripService API end points.
 */
@RestController
public class TripController {

  private static final Logger LOGGER = LoggerFactory.getLogger(TripController.class);

  @Autowired
  TripDealsService tripDealsService;

  /**
   * Get user Trip deals according to attractionId, user preferences and rewards point.
   *
   * @param attractionId for which we want to retrieve trip deals
   * @param rewardPoints of the user
   * @param preferences  of the yser
   * @return List of provider Dto
   */
  @PutMapping("/getTripDeals")
  public List<ProviderDto> getTripDeals(@RequestParam UUID attractionId,
                                        @RequestParam int rewardPoints,
                                        @Valid @RequestBody PreferencesDto preferences) {
    LOGGER.info("Request: Get trip deals for attraction {}", attractionId);
    List<ProviderDto> providers = tripDealsService.getUserTripDeals(attractionId, preferences, rewardPoints);
    LOGGER.info("Response: Trip deals for attraction {} sent", attractionId);
    return providers;
  }

}
