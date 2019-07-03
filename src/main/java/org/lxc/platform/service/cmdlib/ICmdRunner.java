package org.lxc.platform.service.cmdlib;

@FunctionalInterface
public interface ICmdRunner {
  String run(String cmd);
}
