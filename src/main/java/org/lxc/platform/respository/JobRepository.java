package org.lxc.platform.respository;

import java.util.Optional;
import org.lxc.platform.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Integer> {
  Optional<Job> findByKey(String key);
}
