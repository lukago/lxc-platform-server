package org.paas.lxc.dev;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.devtools.restart.Restarter;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;
import org.zeroturnaround.exec.ProcessExecutor;
import org.zeroturnaround.exec.stream.slf4j.Slf4jStream;

@Component
public class NpmRunner implements CommandLineRunner {

  private static Logger log = LoggerFactory.getLogger(NpmRunner.class);
  private Environment environment;

  @Autowired
  public NpmRunner(Environment environment) {
    this.environment = environment;
  }

  @Override
  public void run(String... args) throws Exception {
    if (!environment.acceptsProfiles(Profiles.of("production")) && !environment
        .acceptsProfiles(Profiles.of("test"))) {
      var registered = (AtomicBoolean) Restarter.getInstance()
          .getOrAddAttribute("npmStarted", AtomicBoolean::new);
      boolean alreadyRun = registered.getAndSet(true);
      if (!alreadyRun) {
        startFrontend();
      }
    }
  }

  private void npmStart(File frontendDir) throws IOException {
    ProcessExecutor process = command("npm", "start")
        .directory(frontendDir)
        .redirectOutput(Slf4jStream.of(LoggerFactory.getLogger("npm")).asInfo())
        .redirectError(Slf4jStream.of(LoggerFactory.getLogger("npm")).asError());

    process.start();
  }

  private void startFrontend() throws IOException {
    File frontendDir = locate("./frontend/package.json", "../frontend/package.json");
    log.info("Starting frontend...");
    npmStart(frontendDir);
  }

  private ProcessExecutor command(String... cmd) {
    if (isWindows()) {
      List<String> args = new ArrayList<>(Arrays.asList("cmd", "/c"));
      args.addAll(Arrays.asList(cmd));
      return new ProcessExecutor().command(args);
    }
    return new ProcessExecutor().command(Arrays.asList(cmd));
  }

  private File locate(String... paths) {
    for (String path : paths) {
      File file = new File(path);
      if (file.isFile()) {
        return file.getParentFile();
      }
    }
    throw new IllegalStateException("Could not locate project");
  }

  private boolean isWindows() {
    return System.getProperty("os.name").toLowerCase().contains("win");
  }

}
