package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;

public class PasswordForceDto {

  @ApiModelProperty
  private String password;

  @ApiModelProperty
  private String passwordRetype;

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getPasswordRetype() {
    return passwordRetype;
  }

  public void setPasswordRetype(String passwordRetype) {
    this.passwordRetype = passwordRetype;
  }
}
