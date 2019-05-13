package org.paas.lxc.dto;

import io.swagger.annotations.ApiModelProperty;

public class ContainerDto {

  @ApiModelProperty
  private String name;

  @ApiModelProperty
  private UserDto owner;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public UserDto getOwner() {
    return owner;
  }

  public void setOwner(UserDto owner) {
    this.owner = owner;
  }
}
