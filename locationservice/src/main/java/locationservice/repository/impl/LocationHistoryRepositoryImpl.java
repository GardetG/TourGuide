package locationservice.repository.impl;

import gpsUtil.location.VisitedLocation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;
import locationservice.repository.LocationHistoryRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository Class implementation for users' visited locations history.
 * Database connection will be used for external users locations, but for testing purposes internal
 * users locations are provided and stored in memory by this implementation.
 */
@Repository
public class LocationHistoryRepositoryImpl implements LocationHistoryRepository {

  private final ConcurrentMap<UUID, List<VisitedLocation>> internalUserLocationsMap = new ConcurrentHashMap<>();

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VisitedLocation> findAll() {
    return internalUserLocationsMap.values()
        .stream()
        .flatMap(Collection::stream)
        .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public List<VisitedLocation> findById(UUID userId) {
    return internalUserLocationsMap.getOrDefault(userId, new ArrayList<>())
        .stream()
        .sorted(Comparator.comparing(visitedLocation -> visitedLocation.timeVisited))
        .collect(Collectors.toList());
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VisitedLocation save(VisitedLocation visitedLocation) {
    List<VisitedLocation> userLocations = findById(visitedLocation.userId);
    userLocations.add(visitedLocation);
    internalUserLocationsMap.put(visitedLocation.userId, userLocations);
    return visitedLocation;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void deleteAll() {
    internalUserLocationsMap.clear();
  }

}
