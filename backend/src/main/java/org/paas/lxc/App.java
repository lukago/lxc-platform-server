package org.paas.lxc;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;

@SpringBootApplication(exclude = SessionAutoConfiguration.class)
public class App {

  public static void main(String[] args) throws Exception {
    create();
  }

  public static void create() throws IOException, InterruptedException {
    var username = "lol";
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


    String lxcAttachCmd = String.format("lxc-attach -n %s --clear-env "
        + "-- bash -c 'apt-get -y install openssh-server; useradd -m user; "
        + "echo root:haslo123 | chpasswd; echo user:haslo123 | chpasswd; echo done'", username);
    Process lxcAttach = Runtime.getRuntime().exec(new String[]{"/bin/bash", "-c", lxcAttachCmd});
    var inAttach = new BufferedReader(new InputStreamReader(lxcAttach.getInputStream()));
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
