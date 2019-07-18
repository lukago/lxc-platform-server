package org.lxc.platform.respository;

import java.util.List;
import java.util.Optional;
import org.lxc.platform.model.Job;
import org.lxc.platform.model.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, Long> {
  Optional<Job> findByKey(String key);

  List<Job> findAllByOrderByStartDateDesc(Pageable pageable);

  List<Job> findAllByCreatedByOrderByStartDateDesc(User user, Pageable pageable);
}
