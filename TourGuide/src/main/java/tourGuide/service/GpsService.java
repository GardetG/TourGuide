package tourGuide.service;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import java.util.Map;
import org.springframework.stereotype.Service;

/**
 * Service Interface to retrieve users and attractions location and manage distance calculation.
 */
@Service
public interface GpsService {

  Map<Attraction, Double> getAttractionsWithDistances(Location location);

  Map<Attraction, Double> getTopNearbyAttractionsWithDistances(Location location, int top);

  double getDistance(Location loc1, Location loc2);

}
