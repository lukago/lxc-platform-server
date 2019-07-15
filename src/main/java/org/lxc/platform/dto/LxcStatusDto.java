package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;

public class LxcStatusDto {

  @ApiModelProperty
  private String statusResult;

  @ApiModelProperty
  private UserSafeDto owner;

  @ApiModelProperty
  private int port;

  @ApiModelProperty
  private String name;

  public String getStatusResult() {
    return statusResult;
  }

  public void setLxcStatus(String statusResult) {
    this.statusResult = statusResult;
  }

  public UserSafeDto getOwner() {
    return owner;
  }

  public void setOwner(UserSafeDto owner) {
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
