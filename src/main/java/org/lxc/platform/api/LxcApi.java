package org.lxc.platform.api;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import org.lxc.platform.dto.ContainerDto;
import org.lxc.platform.dto.JobDto;
import org.lxc.platform.dto.LxcCreateDto;
import org.lxc.platform.dto.LxcStatusDto;
import org.lxc.platform.dto.ServerInfoDto;
import org.lxc.platform.model.Role;
import org.lxc.platform.service.LxcService;
import org.lxc.platform.service.UserService;
import org.lxc.platform.service.flow.IProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lxc")
@Api(tags = "lxc")
public class LxcApi {

  private static Logger LOG = LoggerFactory.getLogger(LxcApi.class);

  private final LxcService lxcService;
  private final UserService userService;
  private final SimpMessagingTemplate template;
  private final IProcessor<JobDto> processor;
  private final Gson gson;

  @Autowired
  public LxcApi(LxcService lxcService,
      UserService userService,
      SimpMessagingTemplate template,
      IProcessor<JobDto> processor) {
    this.lxcService = lxcService;
    this.userService = userService;
    this.template = template;
    this.processor = processor;
    this.gson = new Gson();

    this.processor.subscribe(job -> {
      var jobJson = this.gson.toJson(job);
      LOG.info("LXC: {}", jobJson);

      if (!job.getCreatedBy().getRoles().contains(Role.ROLE_ADMIN)) {
        this.template.convertAndSendToUser(job.getCreatedBy().getUsername(), "/sc/topic/jobs", jobJson);
      }

      userService.getAllAdmins().forEach(admin -> {
        this.template.convertAndSendToUser(admin.getUsername(), "/sc/topic/jobs", jobJson);
      });
    });
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  public ResponseEntity<Void> createLxc(
      HttpServletRequest req,
      @ApiParam("lxcCreateDto") @RequestBody @Valid LxcCreateDto lxcCreateDto) {
    lxcService.create(
        userService.whoamiInner(req),
        lxcCreateDto.getName(),
        Integer.valueOf(lxcCreateDto.getPort())
    );

    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  @RequestMapping("/{lxcName}/assign")
  public ResponseEntity<Void> assignLxcToUser(
      @ApiParam("lxcName") @PathVariable @Pattern(regexp = "^[A-Za-z0-9]+$") String lxcName,
      @ApiParam("username") @RequestParam  String username
  ) {
    lxcService.assignUserToLxc(lxcName, username);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  @RequestMapping("/{lxcName}/unassign")
  public ResponseEntity<Void> unassingLxcFromUser(
      @ApiParam("lxcName") @PathVariable @Pattern(regexp = "^[A-Za-z0-9]+$") String lxcName
  ) {
    lxcService.unassignLxcFromUser(lxcName);
    return new ResponseEntity<>(HttpStatus.OK);
  }

  @GetMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  public ResponseEntity<List<ContainerDto>> getLxcs() {
    var containers = lxcService.getAllContainers();
    return new ResponseEntity<>(containers, HttpStatus.OK);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  @RequestMapping("/{lxcName}/start")
  public ResponseEntity<String> startLxc(
      HttpServletRequest req,
      @ApiParam("lxcName") @PathVariable @Pattern(regexp = "^[A-Za-z0-9]+$") String lxcName
  ) {
    lxcService.startLxcAdmin(userService.whoamiInner(req), lxcName);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  @RequestMapping("/{lxcName}/stop")
  public ResponseEntity<String> stopLxc(
      HttpServletRequest req,
      @ApiParam("lxcName") @PathVariable @Pattern(regexp = "^[A-Za-z0-9]+$") String lxcName
  ) {
    lxcService.stopLxcAdmin(userService.whoamiInner(req), lxcName);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @GetMapping(value = "/{lxcName}/status")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<LxcStatusDto> getLxcStatus(
      HttpServletRequest req,
      @PathVariable @Pattern(regexp = "^[A-Za-z0-9]+$") String lxcName) {
    lxcService.getLxcStatusAdmin(userService.whoamiInner(req), lxcName);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @GetMapping(value = "/server")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
  @ApiOperation(value = "", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<ServerInfoDto> getServerInfo() {
    return new ResponseEntity<>(lxcService.getServerInfo(), HttpStatus.OK);
  }

  @MessageMapping("sc/jobs")
  @SendTo("/sc/topic/jobs")
  public String jobMessage(String msg) {
    LOG.info("Socket jobMessage: {}", msg);
    return msg;
  }
}
