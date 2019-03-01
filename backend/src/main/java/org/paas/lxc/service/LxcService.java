package org.paas.lxc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class LxcService {

  private static Logger log = LoggerFactory.getLogger(LxcService.class);
  /**
   * lxc-create -n cont -t download -- --dist ubuntu --release bionic --arch amd64
   * lxc-start -n cont
   * lxc-attach -n cont --clear-env
   *   apt-get -y install openssh-server
   *   useradd -m user
   *   echo root:haslo123 | chpasswd
   *   echo user:haslo123 | chpasswd
   *   exit
   * ip = lxc-info -n cont -iH
   * state = lxc-info -n cont -sH
   * pid = lxc-info -n cont -pH
   *
   * ssh user@ip
   *
   */
  public void create(String username) throws IOException, InterruptedException {
    log.info("creating lxc for %s", username);
    String lxcCreateCmd = String.format("lxc-create -n %s -t download -- "
        + "--dist ubuntu --release bionic --arch amd64", username);
    Process lxcCreate = Runtime.getRuntime().exec(lxcCreateCmd);
    var inCreate = new BufferedReader(new InputStreamReader(lxcCreate.getInputStream()));
    while (inCreate.ready()) log.info(inCreate.readLine());
    lxcCreate.waitFor();

    String lxcStartCmd = String.format("lxc-start -n %s", username);
    Process lxcStart = Runtime.getRuntime().exec(lxcStartCmd);
    lxcStart.waitFor();

    String lxcAttachCmd = String.format("lxc-attach -n %s --clear-env", username);
    Process lxcAttach = Runtime.getRuntime().exec(lxcAttachCmd);
    var outAttach = new PrintStream(lxcAttach.getOutputStream());
    var inAttach = new BufferedReader(new InputStreamReader(lxcAttach.getInputStream()));
    outAttach.println("apt-get -y install openssh-server; useradd -m user; echo root:haslo123 | chpasswd"
        + "echo user:haslo123 | chpasswd; exit;");
    while (inAttach.ready()) log.info(inAttach.readLine());
    lxcAttach.waitFor();

    String getIpCmd = String.format("lxc-info -n %s -iH", username);
    Process getIp = Runtime.getRuntime().exec(getIpCmd);
    var inGetIp = new BufferedReader(new InputStreamReader(getIp.getInputStream()));
    String ip = inGetIp.readLine();
    log.info("Ip: %s", ip);
    getIp.waitFor();

    log.info("ssh user@" + ip);
  }

  public void create() throws IOException, InterruptedException {
    var username = "avc";
    String line = "";
    System.out.println("creating lxc for " + username);
    String lxcCreateCmd = String.format("lxc-create -n %s -t download -- "
        + "--dist ubuntu --release bionic --arch amd64", username);
    Process lxcCreate = Runtime.getRuntime().exec(lxcCreateCmd);
    var inCreate = new BufferedReader(new InputStreamReader(lxcCreate.getInputStream()));
    while ((line = inCreate.readLine()) != null) System.out.println(line);
    lxcCreate.waitFor();

    String lxcStartCmd = String.format("lxc-start -n %s", username);
    Process lxcStart = Runtime.getRuntime().exec(lxcStartCmd);
    lxcStart.waitFor();

    String lxcAttachCmd = String.format("lxc-attach -n %s --clear-env", username);
    Process lxcAttach = Runtime.getRuntime().exec(lxcAttachCmd);
    var outAttach = new PrintStream(lxcAttach.getOutputStream());
    var inAttach = new BufferedReader(new InputStreamReader(lxcAttach.getInputStream()));
    outAttach.println("apt-get -y install openssh-server; useradd -m user; echo root:haslo123 | chpasswd"
        + "echo user:haslo123 | chpasswd; exit;");
    while ((line = inAttach.readLine()) != null) System.out.println(line);
    lxcAttach.waitFor();

    String getIpCmd = String.format("lxc-info -n %s -iH", username);
    Process getIp = Runtime.getRuntime().exec(getIpCmd);
    var inGetIp = new BufferedReader(new InputStreamReader(getIp.getInputStream()));
    String ip = inGetIp.readLine();
    System.out.println(ip);
    getIp.waitFor();

    System.out.println("ssh user@" + ip);
  }
}
