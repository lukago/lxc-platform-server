package org.lxc.platform.service;

import org.lxc.platform.service.cmdlib.ICmdConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CmdConfig implements ICmdConfig {
  @Value("${ssh.server.ip:@null}")
  private String serverIp;

  @Value("${ssh.server.port:@null}")
  private String serverPort;

  @Value("${ssh.server.key:@null}")
  private String key;

  @Value("${ssh.server.username:@null}")
  private String username;

  @Value("${ssh.server.password:@null}")
  private String password;

  @Value("${ssh.server.parentName:@null}")
  private String parentName;

  @Value("${ssh.lxc.port:@null}")
  private String lxcPort;

  @Value("${lxc.cmdRunner:@null}")
  private String cmdRunnerQualifier;

  @Value("${lxc.copy:@null}")
  private String copyCmd;

  @Value("${lxc.start:@null}")
  private String startCmd;

  @Value("${lxc.stop:@null}")
  private String stopCmd;

  @Value("${lxc.info:@null}")
  private String infoCmd;

  @Value("${lxc.routing:@null}")
  private String routingCmd;

  public String getServerIp() {
    return serverIp;
  }

  public String getServerPort() {
    return serverPort;
  }

  public String getKey() {
    return key;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public String getParentName() {
    return parentName;
  }

  public String getLxcPort() {
    return lxcPort;
  }

  public String getCmdRunnerQualifier() {
    return cmdRunnerQualifier;
  }

  public String getCopyCmd() {
    return copyCmd;
  }

  public String getStartCmd() {
    return startCmd;
  }

  public String getStopCmd() {
    return stopCmd;
  }

  public String getInfoCmd() {
    return infoCmd;
  }

  @Override
  public String getRoutingCmd() {
    return routingCmd;
  }
}
