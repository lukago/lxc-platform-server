package org.lxc.platform;

import java.io.File;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;

public abstract class AbstractTest {

  protected static final DockerComposeContainer SERVER_CONTAINER;
  protected static final int SERVER_PORT = 8085;
  protected static final String SERVER_NAME = "lxc-platform-server-test";

  static {
    SERVER_CONTAINER = new
        DockerComposeContainer(new File("docker-compose.test.yml"))
        .withExposedService(SERVER_NAME, SERVER_PORT, Wait.forListeningPort());

    SERVER_CONTAINER.start();
  }
}
