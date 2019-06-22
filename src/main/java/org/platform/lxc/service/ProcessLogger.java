package org.platform.lxc.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessLogger {

  private static Logger log = LoggerFactory.getLogger(ProcessLogger.class);

  public static void logProcess(Process process, String cmd) throws IOException {
    log.info("> " + cmd);

    String line;
    var br = new BufferedReader(new InputStreamReader(process.getInputStream()));
    while ((line = br.readLine()) != null) {
      log.info(line);
    }
  }

  public static String readAndLogProcess(Process process, String cmd) throws IOException {
    log.info("> " + cmd);

    String line;
    var sb = new StringBuilder();
    var br = new BufferedReader(new InputStreamReader(process.getInputStream()));
    while ((line = br.readLine()) != null) {
      log.info(line);
      sb.append(line);
      if (!line.endsWith("\n")) {
        sb.append("\n");
      }
    }

    return sb.toString();
  }

}
