package userservice.repository.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import org.springframework.stereotype.Repository;
import userservice.domain.User;
import userservice.repository.UserRepository;

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
    if (internalUserMap.containsKey(user.getUserName())) {
      return null;
    }
    User userToAdd = User.of(user, UUID.randomUUID());
    internalUserMap.put(userToAdd.getUserName(), userToAdd);
    return userToAdd;
  }

  @Override
  public void deleteAll() {
    internalUserMap.clear();
  }

}
