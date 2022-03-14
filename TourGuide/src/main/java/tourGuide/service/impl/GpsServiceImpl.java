package tourGuide.service.impl;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tourGuide.config.TourGuideProperties;
import tourGuide.dto.AttractionDto;
import tourGuide.dto.LocationDto;
import tourGuide.dto.VisitedLocationDto;
import tourGuide.exception.NoLocationFoundException;
import tourGuide.repository.LocationHistoryRepository;
import tourGuide.service.GpsService;
import tourGuide.utils.AttractionMapper;
import tourGuide.utils.LocationMapper;

/**
 * Service implementation class to retrieve users and attractions location and manage distance
 * calculation.
 */
@Service
public class GpsServiceImpl implements GpsService {

  private int proximityBuffer = 10;
  private final GpsUtil gpsUtil;
  private final LocationHistoryRepository locationHistoryRepository;

  @Autowired
  public GpsServiceImpl(GpsUtil gpsUtil, TourGuideProperties properties,
                        LocationHistoryRepository locationHistoryRepository) {
    this.gpsUtil = gpsUtil;
    this.locationHistoryRepository = locationHistoryRepository;
  }

  private Map<Attraction, Double> getAttractionsWithDistances(Location location) {
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

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<AttractionDto, VisitedLocationDto> getVisitedAttractions(UUID userId) {
    List<Attraction> attractions = gpsUtil.getAttractions();
    List<VisitedLocation> visitedLocations = locationHistoryRepository.findById(userId);
    Map<AttractionDto, VisitedLocationDto>  visitedAttraction = new HashMap<>();
    attractions.forEach(attraction -> visitedLocations
        .stream()
        .filter(visitedLocation -> nearAttraction(visitedLocation, attraction))
        .findFirst()
        .ifPresent(visitedLocation -> visitedAttraction.put(
            AttractionMapper.toDto(attraction),
            new VisitedLocationDto(
                visitedLocation.userId,
                LocationMapper.toDto(visitedLocation.location),
                visitedLocation.timeVisited
            )
            ))
    );
    return visitedAttraction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Map<AttractionDto, Double> getNearbyAttractions(UUID userId, int limit)
      throws NoLocationFoundException {
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }
    VisitedLocation visitedLocation = locationHistoryRepository.findFirstByIdOrderByDateDesc(userId)
        .orElseThrow(() ->  new NoLocationFoundException("No location registered for the user yet"));
    return getAttractionsWithDistances(visitedLocation.location).entrySet()
        .stream()
        .sorted(Comparator.comparingDouble(Map.Entry::getValue))
        .limit(limit)
        .collect(Collectors.toMap(e -> AttractionMapper.toDto(e.getKey()), Map.Entry::getValue, (e1,e2) -> e1, LinkedHashMap::new));
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

  private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    return getDistance(attraction, visitedLocation.location) < proximityBuffer;
  }

}
