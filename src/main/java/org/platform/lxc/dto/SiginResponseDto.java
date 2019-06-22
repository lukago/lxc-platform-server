package org.platform.lxc.dto;

import io.swagger.annotations.ApiModelProperty;

public class SiginResponseDto {

  @ApiModelProperty
  private String token;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
