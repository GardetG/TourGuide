package tourGuide.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;
import tourGuide.domain.UserReward;

/**
 * Repository Class for User rewards.
 */
@Repository
public interface RewardsRepository {

  List<UserReward> findById(UUID userId);

  UserReward save(UserReward reward);

}
