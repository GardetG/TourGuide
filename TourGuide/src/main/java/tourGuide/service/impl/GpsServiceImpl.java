package tourGuide.service.impl;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.config.TourGuideProperties;
import tourGuide.service.GpsService;
import tripPricer.TripPricer;

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
    return null;
  }

  @Override
  public Map<Attraction, Double> getTopNearbyAttractionsWithDistances(Location location, int top) {
    return null;
  }

}
