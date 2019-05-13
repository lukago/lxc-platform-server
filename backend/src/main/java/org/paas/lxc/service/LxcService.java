package org.paas.lxc.service;

import java.util.Date;
import java.util.UUID;
import javax.transaction.Transactional;
import org.paas.lxc.model.Job;
import org.paas.lxc.model.JobStatus;
import org.paas.lxc.respository.ContainerRepository;
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

  @Autowired
  ContainerRepository containerRepository;

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


  private Cmds initCmds(String name, String username, String password) {
    Cmds cmds = new Cmds();

    cmds.name = name;
    cmds.username = username;
    cmds.lxcCreateCmd = String.format("lxc-create -n %s -t download -- "
        + "--dist ubuntu --release bionic --arch amd64", name);
    cmds.lxcAttachCmd = String.format("lxc-attach -n %s --clear-env "
        + "-- bash -c 'echo nameserver 212.51.192.2 >> /etc/resolv.conf; "
        + "echo nameserver 8.8.8.8 >> /etc/resolv.conf; "
        + "apt-get -y install openssh-server; useradd -m user; "
        + "echo root:haslo123 | chpasswd; echo %s:%s | chpasswd; echo done'",
        name, username, password);
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
