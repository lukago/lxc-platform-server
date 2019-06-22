package org.platform.lxc.respository;

import java.util.Optional;
import org.platform.lxc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {

  Boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);

  void deleteByUsername(String username);
}
