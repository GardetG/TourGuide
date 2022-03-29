package tourguideservice.repository;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tourguideservice.domain.User;

/**
 * Repository Class for User entity.
 */
@Repository
public interface UserRepository {

  List<User> findAll();

  Optional<User> findByUsername(String userName);

  User save(User user);

  void deleteAll();

}