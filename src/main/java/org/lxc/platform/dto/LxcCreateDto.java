package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import javax.validation.constraints.Pattern;

public class LxcCreateDto {

  @ApiModelProperty
  @Pattern(regexp = "^[A-Za-z0-9]+$")
  private String name;

  @ApiModelProperty
  @Pattern(regexp = "^[0-9]+$")
  private String port;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getPort() {
    return port;
  }

  public void setPort(String port) {
    this.port = port;
  }
}
