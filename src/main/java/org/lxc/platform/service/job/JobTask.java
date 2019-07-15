package org.lxc.platform.service.job;

import org.lxc.platform.dto.JobDto;

public interface JobTask<T> {
  JobDto getJob();
  T run();
}
