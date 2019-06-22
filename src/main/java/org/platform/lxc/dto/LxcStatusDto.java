package org.platform.lxc.dto;

import io.swagger.annotations.ApiModelProperty;

public class LxcStatusDto {

  @ApiModelProperty
  private String lxcStatus;

  @ApiModelProperty
  private UserSafeDto owner;

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
}
