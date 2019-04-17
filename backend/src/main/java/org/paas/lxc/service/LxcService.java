package org.paas.lxc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LxcService {

  private static Logger log = LoggerFactory.getLogger(LxcService.class);

  /**
   * lxc-create chain
   *
   * @param username user to create lxc for
   */
  public void create(String username) throws IOException, InterruptedException {
    log.info("creating lxc for {}", username);

    Cmds cmds = initCmds(username);

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

  private class Cmds {

    String lxcCreateCmd;
    String lxcAttachCmd;
    String[] lxcAttachCmdBash;
    String lxcStartCmd;
    String lxcStopCmd;
    String getIpCmd;
  }
}
