package org.lxc.platform.service;

import org.lxc.platform.service.cmdlib.ICmdConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CmdConfig implements ICmdConfig {
  @Value("${ssh.server.ip}")
  private String serverIp;

  @Value("${ssh.server.port}")
  private String serverPort;

  @Value("${ssh.server.key}")
  private String key;

  @Value("${ssh.server.username}")
  private String username;

  @Value("${ssh.server.password}")
  private String password;

  @Value("${ssh.server.parentName}")
  private String parentName;

  @Value("${ssh.lxc.port}")
  private String lxcPort;

  @Value("${lxc.cmdRunner}")
  private String cmdRunnerQualifier;

  @Value("${lxc.copy}")
  private String copyCmd;

  @Value("${lxc.start}")
  private String startCmd;

  @Value("${lxc.stop}")
  private String stopCmd;

  @Value("${lxc.getIp}")
  private String getIpCmd;

  @Value("${lxc.info}")
  private String infoCmd;

  @Value("${lxc.sudo}")
  private String sudoCmd;

  @Value("${lxc.setIpTableRouting}")
  private String setIpTableRoutingCmd;

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

  public String getGetIpCmd() {
    return getIpCmd;
  }

  public String getSetIpTableRoutingCmd() {
    return setIpTableRoutingCmd;
  }

  public String getInfoCmd() {
    return infoCmd;
  }

  public String getSudoCmd() {
    return sudoCmd;
  }
}
