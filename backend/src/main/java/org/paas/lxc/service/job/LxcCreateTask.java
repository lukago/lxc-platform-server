package org.paas.lxc.service.job;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import org.modelmapper.ModelMapper;
import org.paas.lxc.dto.JobDto;
import org.paas.lxc.model.Container;
import org.paas.lxc.model.Job;
import org.paas.lxc.model.JobStatus;
import org.paas.lxc.respository.ContainerRepository;
import org.paas.lxc.respository.JobRepository;
import org.paas.lxc.service.LxcService.Cmds;
import org.reactivestreams.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LxcCreateTask implements JobTask {

  private static Logger log = LoggerFactory.getLogger(LxcCreateTask.class);

  private Job job;
  private Cmds cmds;
  private Processor<Job, Job> processor;
  private JobRepository jobRepository;
  private ContainerRepository containerRepository;

  public LxcCreateTask(Job job, Cmds cmds, Processor<Job, Job> processor,
      JobRepository jobRepository, ContainerRepository containerRepository) {
    this.job = job;
    this.cmds = cmds;
    this.processor = processor;
    this.jobRepository = jobRepository;
    this.containerRepository = containerRepository;
  }

  @Override
  public Job getJob() {
    return job;
  }

  @Override
  public void run() {
    job.setJobStatus(JobStatus.IN_PROGRESS);
    job = jobRepository.save(job);
    processor.onNext(job);

    try {
      create();

      Container container = new Container();
      container.setName(cmds.name);
      container.setRunning(false);
      containerRepository.save(container);

      job.setJobStatus(JobStatus.DONE);
      job.setEndDate(new Date());
      job = jobRepository.save(job);

      processor.onNext(job);
    } catch (Exception e) {
      log.info("Job exception: {}", e);
      job.setJobStatus(JobStatus.FAILED);
      job.setEndDate(new Date());
      job = jobRepository.save(job);
      processor.onNext(job);
      throw new IllegalThreadStateException("Lxc create job exception");
    }
  }

  private void create() throws IOException, InterruptedException {
    Process lxcCreate = Runtime.getRuntime().exec(cmds.lxcCreateCmd);
    logProcess(lxcCreate, cmds.lxcCreateCmd);
    lxcCreate.waitFor();

    Process lxcStart = Runtime.getRuntime().exec(cmds.lxcStartCmd);
    logProcess(lxcStart, cmds.lxcStartCmd);
    lxcStart.waitFor();

    Process lxcStop = Runtime.getRuntime().exec(cmds.lxcStopCmd);
    logProcess(lxcStop, cmds.lxcStopCmd);
    lxcStart.waitFor();

    Process lxcRestart = Runtime.getRuntime().exec(cmds.lxcStartCmd);
    logProcess(lxcRestart, cmds.lxcStartCmd);
    lxcStart.waitFor();

    Process lxcAttach = Runtime.getRuntime().exec(cmds.lxcAttachCmdBash);
    logProcess(lxcAttach, cmds.lxcAttachCmdBash[2]);
    lxcAttach.waitFor();

    Process getIp = Runtime.getRuntime().exec(cmds.getIpCmd);
    String ip = readAndLogProcess(getIp, cmds.getIpCmd);
    getIp.waitFor();

    log.info("ssh user@{}", ip);
  }

  private void logProcess(Process process, String cmd) throws IOException {
    log.info("> " + cmd);

    String line;
    var br = new BufferedReader(new InputStreamReader(process.getInputStream()));
    while ((line = br.readLine()) != null) {
      log.info(line);
    }
  }

  private String readAndLogProcess(Process process, String cmd) throws IOException {
    log.info("> " + cmd);

    String line;
    var sb = new StringBuilder();
    var br = new BufferedReader(new InputStreamReader(process.getInputStream()));
    while ((line = br.readLine()) != null) {
      log.info(line);
      sb.append(line);
    }

    return sb.toString();
  }
}
