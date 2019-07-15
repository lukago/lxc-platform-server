package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;

public class ServerInfoDto {

  @ApiModelProperty
  private String ip;

  @ApiModelProperty
  private String port;

  public String getIp() {
    return ip;
  }

  public void setIp(String ip) {
    this.ip = ip;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }
}
