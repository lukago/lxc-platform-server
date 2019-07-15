package org.lxc.platform.respository;

import java.util.List;
import java.util.Optional;
import org.lxc.platform.model.Role;
import org.lxc.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

  Boolean existsByUsername(String username);

  Optional<User> findByUsername(String username);

  List<User> findAllByRolesContaining(Role role);

  void deleteByUsername(String username);
}
