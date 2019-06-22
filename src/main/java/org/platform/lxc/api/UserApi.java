package org.platform.lxc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.platform.lxc.dto.ContainerDto;
import org.platform.lxc.dto.PasswordDto;
import org.platform.lxc.dto.UserSafeDto;
import org.platform.lxc.dto.UserUpdateDto;
import org.platform.lxc.service.LxcService;
import org.platform.lxc.service.UserService;
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

  @Autowired
  private UserService userService;

  @Autowired
  private ModelMapper modelMapper;

  @Autowired
  private LxcService lxcService;

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
    List<UserSafeDto> userDtos = userService.getAll().stream()
        .map(user -> modelMapper.map(user, UserSafeDto.class))
        .collect(Collectors.toList());

    return new ResponseEntity<>(userDtos, HttpStatus.OK);
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
  public UserSafeDto search(@ApiParam("Username") @PathVariable String username) {
    return modelMapper.map(userService.search(username), UserSafeDto.class);
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
    return new ResponseEntity<>(modelMapper.map(userService.whoami(req), UserSafeDto.class), HttpStatus.OK);
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
    var containers = lxcService.getUserContainers(userService.whoami(req))
        .stream()
        .map(cont -> modelMapper.map(cont, ContainerDto.class))
        .collect(Collectors.toList());

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
  public ResponseEntity<String> getLxcStatus(HttpServletRequest req, @PathVariable String lxcName) {
    String status = lxcService.getLxcStatusForUser(userService.whoami(req), lxcName);

    return new ResponseEntity<>(status, HttpStatus.OK);
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
    String res = lxcService.startLxcForUser(userService.whoami(req), lxcName);
    return new ResponseEntity<>(res, HttpStatus.OK);
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
    String res = lxcService.stopLxcForUser(userService.whoami(req), lxcName);
    return new ResponseEntity<>(res, HttpStatus.OK);
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
    return new ResponseEntity<>(
        modelMapper.map(userService.update(user, username), UserSafeDto.class), HttpStatus.OK);
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
      @RequestBody PasswordDto passowrdDto,
      @PathVariable String username) {
    return new ResponseEntity<>(
        modelMapper.map(userService.updateUserPassword(passowrdDto, username), UserSafeDto.class), HttpStatus.OK);
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
      @RequestBody PasswordDto passowrdDto) {
    return new ResponseEntity<>(modelMapper.map(userService.updateUserPassword(
            passowrdDto, userService.whoami(req).getUsername()), UserSafeDto.class), HttpStatus.OK);
  }



}
