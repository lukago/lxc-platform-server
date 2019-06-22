package org.platform.lxc.service.job;

import org.platform.lxc.model.Job;

public interface JobTask extends Runnable {
  Job getJob();
}
