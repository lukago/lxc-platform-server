package org.platform.lxc.service;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import javax.transaction.Transactional;
import org.platform.lxc.exception.HttpException;
import org.platform.lxc.model.Container;
import org.platform.lxc.model.Job;
import org.platform.lxc.model.JobStatus;
import org.platform.lxc.model.LxcStatus;
import org.platform.lxc.model.User;
import org.platform.lxc.respository.ContainerRepository;
import org.platform.lxc.respository.JobRepository;
import org.platform.lxc.respository.UserRepository;
import org.platform.lxc.service.job.LxcCreateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;

@Service
public class LxcService {

  private static Logger log = LoggerFactory.getLogger(LxcService.class);

  @Autowired
  JobRepository jobRepository;

  @Autowired
  ContainerRepository containerRepository;

  @Autowired
  UserRepository userRepository;

  @Async
  @Transactional
  public void create(String name, String username, String password, EmitterProcessor<Job> processor) {
    log.info("creating lxc for {}", name);
    Cmds cmds = initCmds(name, username, password);

    Job job = new Job();
    job.setStartDate(new Date());
    job.setKey(UUID.randomUUID().toString());
    job.setJobStatus(JobStatus.PENDING);
    job.setDescription("LXC create, name: " + name);
    Job savedJob = jobRepository.save(job);

    LxcCreateTask lxcTask = new LxcCreateTask(
        savedJob,
        cmds,
        processor,
        jobRepository,
        containerRepository
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

  public String getLxcStatusForUser(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    String cmd = String.format("lxc-info -n %s", container.getName());
    return runProcess(cmd);
  }

  public LxcStatus getLxcStatus(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    String cmd = String.format("lxc-info -n %s", container.getName());
    String statusRes = runProcess(cmd);

    LxcStatus lxcStatus = new LxcStatus();
    lxcStatus.setStatusResult(statusRes);
    lxcStatus.setOwner(container.getOwner());

    return lxcStatus;
  }

  public String startLxcForUser(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    String cmd = String.format("lxc-start -n %s", container.getName());
    return runProcess(cmd);
  }

  public String startLxc(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    String cmd = String.format("lxc-start -n %s", container.getName());
    return runProcess(cmd);
  }

  public String stopLxcForUser(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    String cmd = String.format("lxc-stop -n %s", container.getName());
    return runProcess(cmd);
  }

  public String stopLxc(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    String cmd = String.format("lxc-stop -n %s", container.getName());
    return runProcess(cmd);
  }

  private String runProcess(String cmd) {
    String result;

    try {
      Process process = Runtime.getRuntime().exec(cmd);
      result = ProcessLogger.readAndLogProcess(process, cmd);
      process.waitFor();
    } catch (IOException | InterruptedException e) {
      throw new HttpException("Error when running cmd:" + cmd, HttpStatus.BAD_REQUEST);
    }

    return result;
  }

  private Cmds initCmds(String name, String username, String password) {
    Cmds cmds = new Cmds();

    cmds.name = name;
    cmds.username = username;
    cmds.lxcCreateCmd = String.format("lxc-create -n %s -t download -- "
        + "--dist ubuntu --release bionic --arch amd64", name);
    cmds.lxcAttachCmd = String.format("lxc-attach -n %s --clear-env "
            + "-- bash -c 'echo nameserver 212.51.192.2 >> /etc/resolv.conf; "
            + "echo nameserver 8.8.8.8 >> /etc/resolv.conf; "
            + "apt-get -y install openssh-server; useradd -m %s; "
            + "echo root:%s | chpasswd; echo %s:%s | chpasswd; echo done'",
        name, username, password, username, password);
    cmds.lxcAttachCmdBash = new String[]{"/bin/bash", "-c", cmds.lxcAttachCmd};
    cmds.lxcStartCmd = String.format("lxc-start -n %s", name);
    cmds.lxcStopCmd = String.format("lxc-stop -n %s", name);
    cmds.getIpCmd = String.format("lxc-info -n %s -iH", name);

    return cmds;
  }

  public static class Cmds {
    public String name;
    public String username;
    public String lxcCreateCmd;
    public String lxcAttachCmd;
    public String[] lxcAttachCmdBash;
    public String lxcStartCmd;
    public String lxcStopCmd;
    public String getIpCmd;
  }
}
