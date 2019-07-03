package org.lxc.platform.service.cmdlib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.TimeUnit;
import net.schmizz.sshj.SSHClient;
import net.schmizz.sshj.connection.channel.direct.Session;
import net.schmizz.sshj.connection.channel.direct.Session.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SshKeyCmdRunner implements ICmdRunner {

  private static final Logger LOG = LoggerFactory.getLogger(SshKeyCmdRunner.class);

  private final ICmdConfig cmdConfig;

  SshKeyCmdRunner(ICmdConfig cmdConfig) {
    this.cmdConfig = cmdConfig;
  }

  @Override
  public String run(String cmdToRun) {
    try {
      SSHClient ssh = new SSHClient();
      ssh.loadKnownHosts();
      ssh.connect(cmdConfig.getServerIp(), Integer.valueOf(cmdConfig.getServerPort()));
      ssh.authPublickey(System.getProperty(cmdConfig.getKey()));

      Session session = ssh.startSession();
      Command cmd = session.exec(cmdToRun);
      String logs = readAndLogProcess(cmd.getInputStream(), cmdToRun);

      cmd.join(5, TimeUnit.MINUTES);
      LOG.info("Command {} finished with exit status {}", cmd.getExitStatus());

      if (cmd.getExitStatus() != 0) {
        throw new IllegalStateException(
            String.format("Exit status is not 0: %s", cmd.getExitErrorMessage()));
      }

      session.close();
      ssh.close();

      return logs;
    } catch (Exception e) {
      LOG.error("Exception running command {}", cmdToRun);
      throw new IllegalStateException(e.getMessage());
    }
  }

  private String readAndLogProcess(InputStream stream, String cmd) throws IOException {
    LOG.info("> " + cmd);

    String line;
    var sb = new StringBuilder();
    var br = new BufferedReader(new InputStreamReader(stream));
    while ((line = br.readLine()) != null) {
      LOG.info(line);
      sb.append(line);
      if (!line.endsWith("\n")) {
        sb.append("\n");
      }
    }

    return sb.toString();
  }
}
