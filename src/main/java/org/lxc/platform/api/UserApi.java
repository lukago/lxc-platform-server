package org.lxc.platform.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.lxc.platform.dto.ContainerDto;
import org.lxc.platform.dto.LxcStatusDto;
import org.lxc.platform.dto.PasswordDto;
import org.lxc.platform.dto.UserSafeDto;
import org.lxc.platform.dto.UserUpdateDto;
import org.lxc.platform.service.LxcService;
import org.lxc.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@Api(tags = "users")
public class UserApi {

  private final UserService userService;
  private final LxcService lxcService;

  @Autowired
  public UserApi(UserService userService, LxcService lxcService) {
    this.userService = userService;
    this.lxcService = lxcService;
  }

  @GetMapping(value = "")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 404, message = "The user doesn't exist"),
      @ApiResponse(code = 412, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<List<UserSafeDto>> getAll() {
    var userDtoList = userService.getAll();
    return new ResponseEntity<>(userDtoList, HttpStatus.OK);
  }

  @DeleteMapping(value = "/{username}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 404, message = "The user doesn't exist"),
      @ApiResponse(code = 412, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<String> delete(@ApiParam("Username") @PathVariable String username) {
    userService.delete(username);
    return new ResponseEntity<>(username, HttpStatus.OK);
  }

  @GetMapping(value = "/{username}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "", response = UserSafeDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 404, message = "The user doesn't exist"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<UserSafeDto> search(@ApiParam("Username") @PathVariable String username) {
    var user = userService.getUser(username);
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping(value = "/me")
  @PreAuthorize("hasRole('ROLE_ADMIN') or hasRole('ROLE_CLIENT')")
  @ApiOperation(value = "", response = UserSafeDto.class)
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<UserSafeDto> whoami(HttpServletRequest req) {
    return new ResponseEntity<>(userService.whoami(req), HttpStatus.OK);
  }

  @GetMapping(value = "/me/lxc")
  @PreAuthorize("hasRole('ROLE_CLIENT')")
  @ApiOperation(value = "", response = List.class)
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<List<ContainerDto>> getLxcsForUser(HttpServletRequest req) {
    var containers = lxcService.getUserContainers(userService.whoamiInner(req));
    return new ResponseEntity<>(containers, HttpStatus.OK);
  }

  @GetMapping(value = "/me/lxc/{lxcName}/status")
  @PreAuthorize("hasRole('ROLE_CLIENT')")
  @ApiOperation(value = "", response = List.class)
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<LxcStatusDto> getLxcStatus(HttpServletRequest req, @PathVariable String lxcName) {

    lxcService.getLxcStatus(userService.whoamiInner(req), lxcName);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_CLIENT')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  @RequestMapping("/me/lxc/{lxcName}/start")
  public ResponseEntity<String> startLxc(
      HttpServletRequest req,
      @ApiParam("lxcName") @PathVariable String lxcName
  ) {
    lxcService.startLxc(userService.whoamiInner(req), lxcName);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_CLIENT')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  @RequestMapping("/me/lxc/{lxcName}/stop")
  public ResponseEntity<String> stopLxc(
      HttpServletRequest req,
      @ApiParam("lxcName") @PathVariable String lxcName
  ) {
    lxcService.stopLxc(userService.whoamiInner(req), lxcName);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/{username}")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 422, message = "Username is already in use"),
      @ApiResponse(code = 412, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<UserSafeDto> updateUserData(
      @RequestBody UserUpdateDto user,
      @PathVariable String username) {
    return new ResponseEntity<>(userService.update(user, username), HttpStatus.OK);
  }

  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @PostMapping("/{username}/pwd")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 422, message = "Username is already in use"),
      @ApiResponse(code = 412, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<UserSafeDto> updateUserPassword(
      @RequestBody PasswordDto passwordDto,
      @PathVariable String username) {
    return new ResponseEntity<>(userService.updateUserPassword(passwordDto, username), HttpStatus.OK);
  }

  @PreAuthorize("hasRole('ROLE_CLIENT')")
  @PostMapping("me/pwd")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 422, message = "Username is already in use"),
      @ApiResponse(code = 412, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<UserSafeDto> updatePassword(
      HttpServletRequest req,
      @RequestBody PasswordDto passwordDto) {
    var user = userService.updateUserPassword(passwordDto, userService.whoamiInner(req));
    return new ResponseEntity<>(user, HttpStatus.OK);
  }



}
