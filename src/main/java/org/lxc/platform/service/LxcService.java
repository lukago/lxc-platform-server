package org.lxc.platform.service;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.lxc.platform.model.Container;
import org.lxc.platform.model.Job;
import org.lxc.platform.model.JobStatus;
import org.lxc.platform.model.LxcStatus;
import org.lxc.platform.model.User;
import org.lxc.platform.exception.HttpException;
import org.lxc.platform.respository.ContainerRepository;
import org.lxc.platform.respository.JobRepository;
import org.lxc.platform.respository.UserRepository;
import org.lxc.platform.service.cmdlib.CmdRunnerFactory;
import org.lxc.platform.service.cmdlib.ICmdConfig;
import org.lxc.platform.service.job.LxcCreateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;

@Service
public class LxcService {

  private static final Logger LOG = LoggerFactory.getLogger(LxcService.class);

  private final JobRepository jobRepository;
  private final ContainerRepository containerRepository;
  private final UserRepository userRepository;
  private final ICmdConfig cmdConfig;

  @Autowired
  public LxcService(JobRepository jobRepository, ContainerRepository containerRepository,
      UserRepository userRepository, CmdConfig cmdConfig) {
    this.jobRepository = jobRepository;
    this.containerRepository = containerRepository;
    this.userRepository = userRepository;
    this.cmdConfig = cmdConfig;
  }

  @Async
  @Transactional
  public void create(String name, int port, EmitterProcessor<Job> processor) {
    LOG.info("Creating lxc for {}", name);

    Job job = new Job();
    job.setStartDate(new Date());
    job.setKey(UUID.randomUUID().toString());
    job.setJobStatus(JobStatus.PENDING);
    job.setDescription("Create LXC with name: " + name + ", port: " + port);
    Job savedJob = jobRepository.save(job);

    LxcCreateTask lxcTask = new LxcCreateTask(
        savedJob,
        CmdRunnerFactory.create(cmdConfig),
        processor,
        jobRepository,
        containerRepository,
        cmdConfig,
        name,
        port
    );

    lxcTask.run();
  }

  @Transactional
  public void assignUserToLxc(String lxcName, String username) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for given name not found", HttpStatus.NOT_FOUND));
    User user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new HttpException("User for given username not found", HttpStatus.NOT_FOUND));

    container.setOwner(user);
    containerRepository.save(container);
  }

  @Transactional
  public void unassignLxcFromUser(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for given name not found", HttpStatus.NOT_FOUND));

    container.setOwner(null);
    containerRepository.save(container);
  }

  public List<Container> getAllContainers() {
    return containerRepository.findAll();
  }

  public List<Container> getUserContainers(User user) {
    return containerRepository.findAllByOwner(user);
  }

  public LxcStatus getLxcStatusForUser(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    String cmd = String.format(cmdConfig.getInfoCmd(), container.getName());
    String statusRes = CmdRunnerFactory.create(cmdConfig).run(cmd);

    LxcStatus lxcStatus = new LxcStatus();
    lxcStatus.setStatusResult(statusRes);
    lxcStatus.setOwner(container.getOwner());
    lxcStatus.setName(container.getName());
    lxcStatus.setPort(container.getPort());

    return lxcStatus;
  }

  public LxcStatus getLxcStatus(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    String cmd = String.format(cmdConfig.getInfoCmd(), container.getName());
    String statusRes = CmdRunnerFactory.create(cmdConfig).run(cmd);

    LxcStatus lxcStatus = new LxcStatus();
    lxcStatus.setStatusResult(statusRes);
    lxcStatus.setOwner(container.getOwner());
    lxcStatus.setName(container.getName());
    lxcStatus.setPort(container.getPort());

    return lxcStatus;
  }

  public String startLxcForUser(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    String cmd = String.format(cmdConfig.getStartCmd(), container.getName());
    return CmdRunnerFactory.create(cmdConfig).run(cmd);
  }

  public String startLxc(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    String cmd = String.format(cmdConfig.getStartCmd(), container.getName());
    return CmdRunnerFactory.create(cmdConfig).run(cmd);
  }

  public String stopLxcForUser(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    String cmd = String.format(cmdConfig.getStopCmd(), container.getName());
    return CmdRunnerFactory.create(cmdConfig).run(cmd);
  }

  public String stopLxc(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    String cmd = String.format(cmdConfig.getStopCmd(), container.getName());
    return CmdRunnerFactory.create(cmdConfig).run(cmd);
  }
}
