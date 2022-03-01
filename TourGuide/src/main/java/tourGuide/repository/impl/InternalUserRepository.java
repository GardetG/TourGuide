package tourGuide.repository.impl;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import tourGuide.domain.User;
import tourGuide.repository.UserRepository;

/**
 * Repository Class implementation for User entity.
 * Database connection will be used for external users, but for testing purposes internal users are
 * provided and stored in memory by this implementation.
 */
@Repository
public class InternalUserRepository implements UserRepository {

  @Override
  public List<User> findAll() {
    return null;
  }

  @Override
  public Optional<User> findByUsername(String userName) {
    return Optional.empty();
  }

  @Override
  public User save(User user) {
    return null;
  }

  @Override
  public void deleteAll() {

  }

}
