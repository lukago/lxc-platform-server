package org.platform.lxc.dto;

import io.swagger.annotations.ApiModelProperty;

public class CredentialsDto {

  @ApiModelProperty
  private String username;

  @ApiModelProperty
  private String password;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
