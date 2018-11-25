package react.respository;

import org.springframework.data.jpa.repository.JpaRepository;
import react.model.User;

public interface UserRepository extends JpaRepository<User, Integer> {

  Boolean existsByUsername(String username);

  User findByUsername(String username);

  void deleteByUsername(String username);
}
