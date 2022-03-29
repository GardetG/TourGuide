package tourguideservice.proxy;

import java.util.List;
import java.util.UUID;
import javax.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import shared.dto.PreferencesDto;
import shared.dto.ProviderDto;

@FeignClient(value = "trip-service", url = "http://localhost:8081")
public interface TripServiceProxy {

  List<ProviderDto> getTripDeals(@RequestParam UUID attractionId,
                                 @RequestParam int rewardPoints,
                                 @Valid @RequestBody PreferencesDto preferences);

}
