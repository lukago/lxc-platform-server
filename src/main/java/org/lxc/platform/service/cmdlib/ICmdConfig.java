package org.lxc.platform.service.cmdlib;

public interface ICmdConfig {
  String getServerIp();

  String getServerPort();

  String getKey();

  String getUsername();

  String getPassword();

  String getParentName();

  String getLxcPort();

  String getCmdRunnerQualifier();

  String getCopyCmd();

  String getStartCmd();

  String getStopCmd();

  String getGetIpCmd();

  String getSetIpTableRoutingCmd();

  String getInfoCmd();

  String getSudoCmd();
}
