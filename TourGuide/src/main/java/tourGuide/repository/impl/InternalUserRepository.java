package tourGuide.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

  private final ConcurrentMap<String, User> internalUserMap = new ConcurrentHashMap<>();

  @Override
  public List<User> findAll() {
    return new ArrayList<>(internalUserMap.values());
  }

  @Override
  public Optional<User> findByUsername(String userName) {
    return Optional.ofNullable(internalUserMap.get(userName));
  }

  @Override
  public User save(User user) {
    if(!internalUserMap.containsKey(user.getUserName())) {
      internalUserMap.put(user.getUserName(), user);
    }
    return user;
  }

  @Override
  public void deleteAll() {
    internalUserMap.clear();
  }

}
