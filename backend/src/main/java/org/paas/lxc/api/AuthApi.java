package org.paas.lxc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.paas.lxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@Api(tags = "auth")
public class AuthApi extends BaseApi {

  @Autowired
  private UserService userService;

  @PostMapping("/signin")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 422, message = "Invalid username/password supplied")
  })
  public String login(@ApiParam("Username") @RequestParam String username,
      @ApiParam("Password") @RequestParam String password) {
    return userService.signin(username, password);
  }

}
