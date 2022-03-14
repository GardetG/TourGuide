package tourGuide.repository.impl;

import gpsUtil.location.VisitedLocation;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import tourGuide.repository.LocationHistoryRepository;


/**
 * Repository Class implementation for users' visited locations history.
 * Database connection will be used for external users locations, but for testing purposes internal
 * users locations are provided and stored in memory by this implementation.
 */
@Repository
public class LocationHistoryRepositoryImpl implements LocationHistoryRepository {

  private final Map<UUID, List<VisitedLocation>> internalUserLocationsMap = new HashMap<>();

  @Override
  public List<VisitedLocation> findById(UUID userId) {
    return internalUserLocationsMap.getOrDefault(userId, new ArrayList<>());
  }

  @Override
  public Optional<VisitedLocation> findFirstByIdOrderByDateDesc(UUID userId) {
    return findById(userId)
        .stream()
        .max(Comparator.comparing(visitedLocation -> visitedLocation.timeVisited));
  }

  @Override
  public VisitedLocation save(VisitedLocation visitedLocation) {
    List<VisitedLocation> userLocations = findById(visitedLocation.userId);
    userLocations.add(visitedLocation);
    internalUserLocationsMap.put(visitedLocation.userId, userLocations);
    return visitedLocation;
  }

}