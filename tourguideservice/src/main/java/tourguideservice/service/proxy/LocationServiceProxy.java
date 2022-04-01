package tourguideservice.service.proxy;

import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shared.dto.AttractionDto;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

@Service
@FeignClient(value = "location-service", url = "http://localhost:8082")
public interface LocationServiceProxy {

  @GetMapping("/getUserLastVisitedLocation")
  VisitedLocationDto getLastLocation(@RequestParam UUID userId) throws NoLocationFoundException;

  @GetMapping("/getAllUserLastVisitedLocation")
  List<VisitedLocationDto> getLastLocation();

  @GetMapping("/trackUserLocation")
  VisitedLocationDto trackUserLocation(@RequestParam UUID userId);

  @GetMapping("/getAttractions")
  List<AttractionDto> getAttractions();

  @PostMapping("/addVisitedLocation")
  void addVisitedLocation(@RequestBody List<VisitedLocationDto> visitedLocationDto, @RequestParam UUID userId);

  @GetMapping("/getVisitedAttractions")
  List<VisitedAttractionDto> getVisitedAttractions(@RequestParam UUID userId);

  @GetMapping("/getNearbyAttractions")
  List<AttractionWithDistanceDto> getNearbyAttractions(@RequestParam UUID userId, @RequestParam int limit)
      throws NoLocationFoundException ;

}
