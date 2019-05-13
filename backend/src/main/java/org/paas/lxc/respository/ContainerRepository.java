package org.paas.lxc.respository;

import org.paas.lxc.model.Container;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContainerRepository extends JpaRepository<Container, Integer> {
}
