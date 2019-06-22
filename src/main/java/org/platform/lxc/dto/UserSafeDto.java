package org.platform.lxc.dto;

import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import org.platform.lxc.model.Role;

public class UserSafeDto {

  @ApiModelProperty
  private String username;

  @ApiModelProperty
  private String email;

  @ApiModelProperty
  private List<Role> roles;

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
}
