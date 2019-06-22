package org.platform.lxc.respository;

import java.util.List;
import java.util.Optional;
import org.platform.lxc.model.Container;
import org.platform.lxc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Integer> {

  Optional<Container> findByName(String name);

  List<Container> findAllByOwner(User owner);

  Optional<Container> findByNameAndOwner(String name, User owner);
}
