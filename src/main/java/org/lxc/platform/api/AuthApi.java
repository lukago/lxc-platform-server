package org.lxc.platform.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.lxc.platform.dto.CredentialsDto;
import org.lxc.platform.dto.SiginResponseDto;
import org.lxc.platform.dto.UserDto;
import org.lxc.platform.dto.UserSafeDto;
import org.lxc.platform.model.Role;
import org.lxc.platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

  private static final Logger LOG = LoggerFactory.getLogger(AuthApi.class);

  private UserService userService;

  @Autowired
  public AuthApi(
      UserService userService,
      @Value("${init.addAdmin}") boolean addAdmin) {
    this.userService = userService;

    if (addAdmin) {
      UserDto userDto = new UserDto();
      userDto.setUsername("admin");
      userDto.setEmail("admin@adminlxc.org");
      userDto.setRoles(List.of(Role.ROLE_ADMIN, Role.ROLE_CLIENT));
      userDto.setPassword("password");
      userDto.setPasswordRetype("password");

      try {
        signup(userDto);
      } catch (Exception e) {
        LOG.warn("Creation of initial admin user failed. Skipping...");
      }
    }
  }

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
    return new ResponseEntity<>(userService.signup(user), HttpStatus.OK);
  }

}
