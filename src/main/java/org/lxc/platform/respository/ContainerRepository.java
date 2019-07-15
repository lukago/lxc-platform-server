package org.lxc.platform.respository;

import java.util.List;
import java.util.Optional;
import org.lxc.platform.model.Container;
import org.lxc.platform.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Long> {

  Optional<Container> findByName(String name);

  List<Container> findAllByOwner(User owner);

  Optional<Container> findByNameAndOwner(String name, User owner);
}
