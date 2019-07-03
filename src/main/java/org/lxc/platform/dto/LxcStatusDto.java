package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;

public class LxcStatusDto {

  @ApiModelProperty
  private String lxcStatus;

  @ApiModelProperty
  private UserSafeDto owner;

  @ApiModelProperty
  private int port;

  @ApiModelProperty
  private String name;

  public String getLxcStatus() {
    return lxcStatus;
  }

  public void setLxcStatus(String lxcStatus) {
    this.lxcStatus = lxcStatus;
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
