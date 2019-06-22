package org.platform.lxc.dto;

import io.swagger.annotations.ApiModelProperty;

public class ContainerDto {

  @ApiModelProperty
  private String name;

  @ApiModelProperty
  private UserSafeDto owner;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserSafeDto getOwner() {
    return owner;
  }

  public void setOwner(UserSafeDto owner) {
    this.owner = owner;
  }
}
