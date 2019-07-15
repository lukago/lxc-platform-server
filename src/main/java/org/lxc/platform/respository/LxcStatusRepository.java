package org.lxc.platform.respository;

import org.lxc.platform.model.LxcStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LxcStatusRepository extends JpaRepository<LxcStatus, Long> {

}
