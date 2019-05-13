package org.paas.lxc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.paas.lxc.dto.CredentialsDto;
import org.paas.lxc.dto.SiginResponseDto;
import org.paas.lxc.dto.UserDto;
import org.paas.lxc.dto.UserSafeDto;
import org.paas.lxc.model.User;
import org.paas.lxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@Api(tags = "auth")
public class AuthApi {

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

  @PostMapping("/signin")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 422, message = "Invalid username/password supplied")
  })
  public SiginResponseDto login(
      @ApiParam("Credentials") @RequestBody CredentialsDto credentialsDto) {
    var response = new SiginResponseDto();
    response
        .setToken(userService.signin(credentialsDto.getUsername(), credentialsDto.getPassword()));
    return response;
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/signup")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 422, message = "Username is already in use"),
      @ApiResponse(code = 412, message = "Expired or invalid JWT token")
  })
  public UserSafeDto signup(@ApiParam("Signup User") @RequestBody UserDto user) {
    return modelMapper
        .map(userService.signup(modelMapper.map(user, User.class)), UserSafeDto.class);
  }

}
