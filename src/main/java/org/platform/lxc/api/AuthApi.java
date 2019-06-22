package org.platform.lxc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.modelmapper.ModelMapper;
import org.platform.lxc.dto.CredentialsDto;
import org.platform.lxc.dto.SiginResponseDto;
import org.platform.lxc.dto.UserDto;
import org.platform.lxc.dto.UserSafeDto;
import org.platform.lxc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
  public ResponseEntity<SiginResponseDto> login(
      @ApiParam("Credentials") @RequestBody CredentialsDto credentialsDto) {
    var response = new SiginResponseDto();
    response
        .setToken(userService.signin(credentialsDto.getUsername(), credentialsDto.getPassword()));
    return new ResponseEntity<>(response, HttpStatus.OK);
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
  public ResponseEntity<UserSafeDto> signup(@ApiParam("Signup User") @RequestBody UserDto user) {
    return new ResponseEntity<>(modelMapper.map(userService.signup(user), UserSafeDto.class), HttpStatus.OK);
  }

}
