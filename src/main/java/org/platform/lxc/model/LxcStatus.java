package org.platform.lxc.model;

public class LxcStatus {
  private String statusResult;
  private User owner;

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
}
