package tourGuide.service.impl;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.config.TourGuideProperties;
import tourGuide.service.GpsService;

/**
 * Service implementation class to retrieve users and attractions location and manage distance
 * calculation.
 */
@Service
public class GpsServiceImpl implements GpsService {

  private final GpsUtil gpsUtil;

  @Autowired
  public GpsServiceImpl(GpsUtil gpsUtil, TourGuideProperties properties) {
    this.gpsUtil = gpsUtil;
  }

  @Override
  public Map<Attraction, Double> getAttractionsWithDistances(Location location) {
    List<Attraction> attractions = gpsUtil.getAttractions();
    return attractions.stream()
        .collect(Collectors.toMap(
            attraction -> attraction,
            attraction -> getDistance(attraction, location)
        ));
  }

  @Override
  public Map<Attraction, Double> getTopNearbyAttractionsWithDistances(Location location, int top) {
    return getAttractionsWithDistances(location).entrySet()
        .stream()
        .sorted(Comparator.comparingDouble(Map.Entry::getValue))
        .limit(top)
        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
  }

  @Override
  public VisitedLocation getUserLocation(UUID userId) {
    return gpsUtil.getUserLocation(userId);
  }

  @Override
  public double getDistance(Location loc1, Location loc2) {
    double lat1 = Math.toRadians(loc1.latitude);
    double lon1 = Math.toRadians(loc1.longitude);
    double lat2 = Math.toRadians(loc2.latitude);
    double lon2 = Math.toRadians(loc2.longitude);

    double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
        + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

    double nauticalMiles = 60 * Math.toDegrees(angle);
    return  TourGuideProperties.STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
  }

}
