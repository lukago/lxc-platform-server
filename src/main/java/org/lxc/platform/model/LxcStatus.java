package org.lxc.platform.model;

public class LxcStatus {
  private String statusResult;
  private User owner;
  private int port;
  private String name;

  public String getStatusResult() {
    return statusResult;
  }

  public void setStatusResult(String statusResult) {
    this.statusResult = statusResult;
  }

  public User getOwner() {
    return owner;
  }

  public void setOwner(User owner) {
    this.owner = owner;
  }

  public int getPort() {
    return port;
  }

  public void setPort(int port) {
    this.port = port;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
