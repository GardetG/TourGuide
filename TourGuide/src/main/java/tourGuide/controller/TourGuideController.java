package tourGuide.controller;

import com.jsoniter.output.JsonStream;
import gpsUtil.location.VisitedLocation;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tourGuide.domain.User;
import tourGuide.dto.LocationDto;
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
  public Map<UUID, LocationDto> getAllCurrentLocations() {
    return tourGuideService.getAllCurrentLocations();
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

  @PutMapping(value = "/setUserPreferences")
  public String getUserPreferences(@RequestParam String userName,
                                   @Valid @RequestBody UserPreferencesDto userPreferencesDto)
      throws UserNotFoundException {
    return JsonStream.serialize(tourGuideService.setUserPreferences(userName, userPreferencesDto));
  }

  private User getUser(String userName) throws UserNotFoundException {
    return tourGuideService.getUser(userName);
  }


}