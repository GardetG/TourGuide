package tourGuide.service.impl;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.config.TourGuideProperties;
import tourGuide.dto.LocationDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.exception.NoLocationFoundException;
import tourGuide.repository.LocationHistoryRepository;
import tourGuide.service.GpsService;
import tourGuide.utils.LocationMapper;

/**
 * Service implementation class to retrieve users and attractions location and manage distance
 * calculation.
 */
@Service
public class GpsServiceImpl implements GpsService {

  private final GpsUtil gpsUtil;
  private final LocationHistoryRepository locationHistoryRepository;

  @Autowired
  public GpsServiceImpl(GpsUtil gpsUtil, TourGuideProperties properties,
                        LocationHistoryRepository locationHistoryRepository) {
    this.gpsUtil = gpsUtil;
    this.locationHistoryRepository = locationHistoryRepository;
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


  /**
   * {@inheritDoc}
   */
  @Override
  public VisitedLocationDto getLastLocation(UUID userId) throws NoLocationFoundException {
    VisitedLocation lastLocation = locationHistoryRepository.findFirstByIdOrderByDateDesc(userId)
        .orElseThrow(() ->  new NoLocationFoundException("No location registered for the user yet"));
    return new VisitedLocationDto(
        lastLocation.userId,
        LocationMapper.toDto(lastLocation.location),
        lastLocation.timeVisited
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VisitedLocationDto trackUserLocation(UUID userId) {
    VisitedLocation currentLocation = gpsUtil.getUserLocation(userId);
    locationHistoryRepository.save(currentLocation);
    return new VisitedLocationDto(
        currentLocation.userId,
        LocationMapper.toDto(currentLocation.location),
        currentLocation.timeVisited
    );
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addLocation(UUID userId, LocationDto location) {
    VisitedLocation visitedLocation = new VisitedLocation(userId, LocationMapper.toEntity(location), new Date());
    locationHistoryRepository.save(visitedLocation);
  }

  @Override
  public VisitedLocation getUserLocation(UUID userId) {
    return gpsUtil.getUserLocation(userId);
  }

  /**
   * {@inheritDoc}
   */
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
