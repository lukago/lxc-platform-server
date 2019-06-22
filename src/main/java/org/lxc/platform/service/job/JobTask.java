package org.lxc.platform.service.job;

import org.lxc.platform.model.Job;

public interface JobTask extends Runnable {
  Job getJob();
}
