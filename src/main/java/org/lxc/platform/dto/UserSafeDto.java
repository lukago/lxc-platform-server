package org.lxc.platform.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.lxc.platform.model.Role;

public class UserSafeDto {

  @ApiModelProperty
  private String username;

  @ApiModelProperty
  private String email;

  @ApiModelProperty
  private List<Role> roles;

  @ApiModelProperty
  private String version;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

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
