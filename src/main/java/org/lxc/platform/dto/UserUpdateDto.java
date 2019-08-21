package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.lxc.platform.model.Role;

public class UserUpdateDto {

  @ApiModelProperty
  private String email;

  @ApiModelProperty
  private List<Role> roles;

  @ApiModelProperty
  private String version;

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public List<Role> getRoles() {
    return roles;
  }

  public void setRoles(List<Role> roles) {
    this.roles = roles;
  }

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }
}
