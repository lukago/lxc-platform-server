package org.paas.lxc.service;

import java.util.Date;
import java.util.UUID;
import org.paas.lxc.model.Job;
import org.paas.lxc.model.JobStatus;
import org.paas.lxc.respository.JobRepository;
import org.paas.lxc.service.job.LxcCreateTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import reactor.core.publisher.EmitterProcessor;

@Service
public class LxcService {

  private static Logger log = LoggerFactory.getLogger(LxcService.class);

  @Autowired
  JobRepository jobRepository;

  /**
   * lxc-create chain
   *
   * @param username user to create lxc for
   */
  @Async
  public void create(String username, EmitterProcessor<String> processor) {
    log.info("creating lxc for {}", username);
    Cmds cmds = initCmds(username);

    Job job = new Job();
    job.setStartDate(new Date());
    job.setJobStatus(JobStatus.PENDING);
    job.setKey(UUID.randomUUID().toString());
    job.setDescription("LXC create, name: " + username);
    Job savedJob = jobRepository.save(job);

    LxcCreateTask lxcTask = new LxcCreateTask(savedJob, cmds, processor, jobRepository);
    lxcTask.run();
  }


  private Cmds initCmds(String username) {
    Cmds cmds = new Cmds();

    cmds.lxcCreateCmd = String.format("lxc-create -n %s -t download -- "
        + "--dist ubuntu --release bionic --arch amd64", username);
    cmds.lxcAttachCmd = String.format("lxc-attach -n %s --clear-env "
        + "-- bash -c 'echo nameserver 212.51.192.2 >> /etc/resolv.conf; "
        + "echo nameserver 8.8.8.8 >> /etc/resolv.conf; "
        + "apt-get -y install openssh-server; useradd -m user; "
        + "echo root:haslo123 | chpasswd; echo user:haslo123 | chpasswd; echo done'", username);
    cmds.lxcAttachCmdBash = new String[]{"/bin/bash", "-c", cmds.lxcAttachCmd};
    cmds.lxcStartCmd = String.format("lxc-start -n %s", username);
    cmds.lxcStopCmd = String.format("lxc-stop -n %s", username);
    cmds.getIpCmd = String.format("lxc-info -n %s -iH", username);

    return cmds;
  }

  public static class Cmds {
    public String lxcCreateCmd;
    public String lxcAttachCmd;
    public String[] lxcAttachCmdBash;
    public String lxcStartCmd;
    public String lxcStopCmd;
    public String getIpCmd;
  }
}
