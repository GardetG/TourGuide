package tourguideservice.proxy;

import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;
import tourguideservice.config.CustomFeignClientConfiguration;

/**
 * Open Feign Client Proxy for TripService.
 */
@Service
@FeignClient(value = "trip-service", url = "${tourguide.tripservice.url}", configuration = CustomFeignClientConfiguration.class)
public interface TripServiceProxy {

  @PutMapping("/getTripDeals")
  List<ProviderDto> getTripDeals(@RequestParam UUID attractionId,
                                 @RequestBody PreferencesDto preferences,
                                 @RequestParam int rewardPoints);

}
