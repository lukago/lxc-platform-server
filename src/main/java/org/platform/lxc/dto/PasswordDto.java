package org.platform.lxc.dto;

import io.swagger.annotations.ApiModelProperty;

public class PasswordDto {

  @ApiModelProperty
  private String oldPassword;

  @ApiModelProperty
  private String password;

  @ApiModelProperty
  private String passwordRetype;

  public String getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(String oldPassword) {
    this.oldPassword = oldPassword;
  }

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
