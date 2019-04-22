package org.paas.lxc.service.job;

import org.paas.lxc.model.Job;

public interface JobTask extends Runnable {
  Job getJob();
}
