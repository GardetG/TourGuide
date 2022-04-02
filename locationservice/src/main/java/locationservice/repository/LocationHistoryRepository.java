package locationservice.repository;

import gpsUtil.location.VisitedLocation;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Repository Class for users' visited locations history.
 */
@Repository
public interface LocationHistoryRepository {

  /**
   * Find all the visited locations persisted.
   *
   * @return List of visited location
   */
  List<VisitedLocation> findAll();

  /**
   * Find all the persisted visited locations of a user.
   *
   * @param userId of the user
   * @return List of visited location
   */
  List<VisitedLocation> findById(UUID userId);

  /**
   * Persist a visited location.
   *
   * @param visitedLocation to persist
   * @return Visited location persisted.
   */
  VisitedLocation save(VisitedLocation visitedLocation);

  /**
   * Delete all the visited location persisted
   */
  void deleteAll();

}
