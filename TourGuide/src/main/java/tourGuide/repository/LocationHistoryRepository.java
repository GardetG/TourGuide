package tourGuide.repository;

import gpsUtil.location.VisitedLocation;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

/**
 * Repository Class for users' visited locations history.
 */
@Repository
public interface LocationHistoryRepository {

  List<VisitedLocation> findById(UUID userId);

  Optional<VisitedLocation> findFirstByIdOrderByDateDesc(UUID userId);

  VisitedLocation save(VisitedLocation visitedLocation);

}
