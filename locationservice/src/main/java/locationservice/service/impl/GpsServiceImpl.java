package locationservice.service.impl;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import locationservice.config.LocationServiceProperties;
import locationservice.repository.LocationHistoryRepository;
import locationservice.service.GpsService;
import locationservice.utils.AttractionMapper;
import locationservice.utils.VisitedLocationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import shared.dto.AttractionDto;
import shared.dto.AttractionWithDistanceDto;
import shared.dto.VisitedAttractionDto;
import shared.dto.VisitedLocationDto;
import shared.exception.NoLocationFoundException;

/**
 * Service implementation class to retrieve users and attractions location and manage distance
 * calculation.
 */
@Service
public class GpsServiceImpl implements GpsService {

  private final GpsUtil gpsUtil;
  private final LocationServiceProperties properties;
  private final LocationHistoryRepository locationHistoryRepository;

  @Autowired
  public GpsServiceImpl(GpsUtil gpsUtil, LocationServiceProperties properties,
                        LocationHistoryRepository locationHistoryRepository) {
    this.gpsUtil = gpsUtil;
    this.properties = properties;
    this.locationHistoryRepository = locationHistoryRepository;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VisitedLocationDto getLastLocation(UUID userId) throws NoLocationFoundException {
    VisitedLocation lastLocation = findUserLastVisitedLocation(userId);
    return VisitedLocationMapper.toDto(lastLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VisitedLocationDto> getAllLastLocation() {
    return locationHistoryRepository.findAll()
        .stream()
        .collect(Collectors.groupingBy(visitedLocation -> visitedLocation.userId))
        .values()
        .stream()
        .map(this::findMostRecentVisitedLocation)
        .filter(Optional::isPresent)
        .map(Optional::get)
        .map(VisitedLocationMapper::toDto)
        .collect(Collectors.toList());

  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VisitedLocationDto trackUserLocation(UUID userId) {
    VisitedLocation currentLocation = gpsUtil.getUserLocation(userId);
    locationHistoryRepository.save(currentLocation);
    return VisitedLocationMapper.toDto(currentLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void addLocation(VisitedLocationDto visitedLocationDto) {
    VisitedLocation visitedLocation = VisitedLocationMapper.toEntity(visitedLocationDto);
    locationHistoryRepository.save(visitedLocation);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AttractionDto> getAttraction() {
    return gpsUtil.getAttractions()
        .stream()
        .map(AttractionMapper::toDto)
        .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VisitedAttractionDto> getVisitedAttractions(UUID userId) {
    List<Attraction> attractions = gpsUtil.getAttractions();
    List<VisitedLocation> visitedLocations = locationHistoryRepository.findById(userId);
    List<VisitedAttractionDto> visitedAttraction = new ArrayList<>();
    attractions.forEach(attraction -> visitedLocations
        .stream()
        .filter(visitedLocation -> isInRangeOfAttraction(visitedLocation, attraction))
        .findFirst()
        .ifPresent(visitedLocation -> visitedAttraction.add(new VisitedAttractionDto(
            AttractionMapper.toDto(attraction),
            VisitedLocationMapper.toDto(visitedLocation)
        )))
    );
    return visitedAttraction;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<AttractionWithDistanceDto> getNearbyAttractions(UUID userId, int limit)
      throws NoLocationFoundException {
    if (limit < 0) {
      throw new IllegalArgumentException("Limit must be positive");
    }
    VisitedLocation visitedLocation = findUserLastVisitedLocation(userId);
    return getAttractionsWithDistances(visitedLocation.location).entrySet()
        .stream()
        .sorted(Comparator.comparingDouble(Map.Entry::getValue))
        .limit(limit)
        .map(e -> new AttractionWithDistanceDto(AttractionMapper.toDto(e.getKey()), e.getValue()))
        .collect(Collectors.toList());
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
    return LocationServiceProperties.STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
  }

  private boolean isInRangeOfAttraction(VisitedLocation visitedLocation, Attraction attraction) {
    return getDistance(attraction, visitedLocation.location) <
        properties.getProximityThresholdInMiles();
  }

  private Map<Attraction, Double> getAttractionsWithDistances(Location location) {
    List<Attraction> attractions = gpsUtil.getAttractions();
    return attractions.stream()
        .collect(Collectors.toMap(
            attraction -> attraction,
            attraction -> getDistance(attraction, location)
        ));
  }

  private Optional<VisitedLocation> findMostRecentVisitedLocation(List<VisitedLocation> visitedLocations) {
    return visitedLocations
        .stream()
        .max(Comparator.comparing(visitedLocation -> visitedLocation.timeVisited));
  }

  private VisitedLocation findUserLastVisitedLocation(UUID userId) throws NoLocationFoundException {
    return findMostRecentVisitedLocation(locationHistoryRepository.findById(userId))
        .orElseThrow(() -> new NoLocationFoundException("No location registered for the user yet"));
  }

}
