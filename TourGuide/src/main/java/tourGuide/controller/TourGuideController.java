package tourGuide.controller;

import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.domain.User;
import tourGuide.dto.NearbyAttractionsDto;
import tourGuide.dto.ProviderDto;
import tourGuide.dto.UserPreferencesDto;
import tourGuide.exception.UserNotFoundException;
import tourGuide.service.TourGuideService;

@RestController
public class TourGuideController {

  @Autowired
  TourGuideService tourGuideService;

  @RequestMapping("/")
  public String index() {
    return "Greetings from TourGuide!";
  }

  @RequestMapping("/getLocation")
  public String getLocation(@RequestParam String userName) throws UserNotFoundException {
    VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
    return JsonStream.serialize(visitedLocation.location);
  }

  //  TODO: Change this method to no longer return a List of Attractions.
  //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
  //  Return a new JSON object that contains:
  // Name of Tourist attraction,
  // Tourist attractions lat/long,
  // The user's location lat/long,
  // The distance in miles between the user's location and each of the attractions.
  // The reward points for visiting each Attraction.
  //    Note: Attraction reward points can be gathered from RewardsCentral
  @RequestMapping("/getNearbyAttractions")
  public String getNearbyAttractions(@RequestParam String userName) throws UserNotFoundException {
    NearbyAttractionsDto nearbyAttractionsDto = tourGuideService.getNearByAttractions(userName);
    return JsonStream.serialize(nearbyAttractionsDto);
  }

  @RequestMapping("/getRewards")
  public String getRewards(@RequestParam String userName) throws UserNotFoundException {
    return JsonStream.serialize(tourGuideService.getUserRewards(getUser(userName)));
  }

  @RequestMapping("/getAllCurrentLocations")
  public String getAllCurrentLocations() {
    // TODO: Get a list of every user's most recent location as JSON
    //- Note: does not use gpsUtil to query for their current location,
    //        but rather gathers the user's current location from their stored location history.
    //
    // Return object should be the just a JSON mapping of userId to Locations similar to:
    //     {
    //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
    //        ...
    //     }

    return JsonStream.serialize("");
  }

  @RequestMapping("/getTripDeals")
  public String getTripDeals(@RequestParam String userName) throws UserNotFoundException {
    List<ProviderDto> providers = tourGuideService.getTripDeals(userName);
    return JsonStream.serialize(providers);
  }


  @RequestMapping("/getUserPreferences")
  public String getUserPreferences(@RequestParam String userName) throws UserNotFoundException {
    return JsonStream.serialize(tourGuideService.getUserPreferences(userName));
  }

  @RequestMapping(value = "/setUserPreferences", method = PUT)
  public String getUserPreferences(@RequestParam String userName,
                                   @Valid @RequestBody UserPreferencesDto userPreferencesDto)
      throws UserNotFoundException {
    return JsonStream.serialize(tourGuideService.setUserPreferences(userName, userPreferencesDto));
  }

  private User getUser(String userName) throws UserNotFoundException {
    return tourGuideService.getUser(userName);
  }


}