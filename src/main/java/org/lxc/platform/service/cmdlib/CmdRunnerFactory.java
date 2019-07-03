package org.lxc.platform.service.cmdlib;

public class CmdRunnerFactory {

  public static ICmdRunner create(ICmdConfig cmdData) {
    if (cmdData.getCmdRunnerQualifier().equalsIgnoreCase("sshkey")) {
      return new SshKeyCmdRunner(cmdData);
    } else if (cmdData.getCmdRunnerQualifier().equalsIgnoreCase("sshpwd")) {
      return new SshPwdCmdRunner(cmdData);
    } else if (cmdData.getCmdRunnerQualifier().equalsIgnoreCase("mock")) {
      return (cmd) -> "done";
    } else {
      throw new IllegalArgumentException("Unsupported type of cmdRunner: " + cmdData.getCmdRunnerQualifier());
    }
  }
}
