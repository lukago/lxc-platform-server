package org.lxc.platform.service.job;

import java.util.Date;
import org.lxc.platform.model.Container;
import org.lxc.platform.model.Job;
import org.lxc.platform.model.JobStatus;
import org.lxc.platform.respository.ContainerRepository;
import org.lxc.platform.respository.JobRepository;
import org.lxc.platform.service.cmdlib.ICmdConfig;
import org.lxc.platform.service.cmdlib.ICmdRunner;
import org.reactivestreams.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LxcCreateTask implements JobTask {

  private final static Logger LOG = LoggerFactory.getLogger(LxcCreateTask.class);

  private final Job job;
  private final ICmdRunner cmdRunner;
  private final Processor<Job, Job> processor;
  private final JobRepository jobRepository;
  private final ContainerRepository containerRepository;
  private final ICmdConfig cmdData;
  private final String name;
  private final int port;

  public LxcCreateTask(Job job,
      ICmdRunner cmdRunner,
      Processor<Job, Job> processor,
      JobRepository jobRepository,
      ContainerRepository containerRepository,
      ICmdConfig cmdData,
      String name,
      int port) {
    this.job = job;
    this.cmdRunner = cmdRunner;
    this.processor = processor;
    this.jobRepository = jobRepository;
    this.containerRepository = containerRepository;
    this.cmdData = cmdData;
    this.name = name;
    this.port = port;
  }

  @Override
  public Job getJob() {
    return jobRepository.findByKey(job.getKey()).orElse(null);
  }

  @Override
  public void run() {
    job.setJobStatus(JobStatus.IN_PROGRESS);
    Job savedJob = jobRepository.save(job);
    processor.onNext(savedJob);

    try {
      cmdRunner.run(String.format(cmdData.getCopyCmd(), cmdData.getParentName(), name));
      cmdRunner.run(String.format(cmdData.getStartCmd(), name));
      String ip = cmdRunner.run(String.format(cmdData.getGetIpCmd(), name));
      cmdRunner.run(String.format(cmdData.getStopCmd(), name));

      String cmd = String.format(cmdData.getSetIpTableRoutingCmd(), port, ip, cmdData.getLxcPort());
      String sudoCmd = String.format(cmdData.getSudoCmd(), cmdData.getPassword(), cmd);
      cmdRunner.run(sudoCmd);

      Container container = new Container();
      container.setName(name);
      container.setPort(port);
      containerRepository.save(container);

      savedJob.setJobStatus(JobStatus.DONE);
      savedJob.setEndDate(new Date());
      savedJob = jobRepository.save(job);

      processor.onNext(job);
    } catch (Exception e) {
      LOG.info("Job exception: {}", e);
      savedJob.setJobStatus(JobStatus.FAILED);
      savedJob.setEndDate(new Date());
      savedJob = jobRepository.save(savedJob);
      processor.onNext(savedJob);
      throw new IllegalThreadStateException("Lxc create job exception");
    }
  }

}
