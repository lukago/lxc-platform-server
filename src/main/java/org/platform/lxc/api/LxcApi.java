package org.platform.lxc.api;

import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import java.util.stream.Collectors;
import org.modelmapper.ModelMapper;
import org.platform.lxc.dto.ContainerDto;
import org.platform.lxc.dto.JobDto;
import org.platform.lxc.dto.LxcCreateDto;
import org.platform.lxc.dto.LxcStatusDto;
import org.platform.lxc.model.Job;
import org.platform.lxc.service.LxcService;
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
import reactor.core.publisher.EmitterProcessor;

@RestController
@RequestMapping("/api/lxc")
@Api(tags = "lxc")
public class LxcApi {

  private static Logger log = LoggerFactory.getLogger(LxcApi.class);

  @Autowired
  LxcService lxcService;

  @Autowired
  private SimpMessagingTemplate template;

  @Autowired
  private ModelMapper modelMapper;

  private Gson gson = new Gson();

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  public ResponseEntity<Void> createLxc(@ApiParam("lxcCreateDto") @RequestBody LxcCreateDto lxcCreateDto) {
    EmitterProcessor<Job> processor = EmitterProcessor.create();

    processor.subscribe(job -> {
      String jobJson = gson.toJson(modelMapper.map(job, JobDto.class));
      log.info("LXC: {}", jobJson);
      template.convertAndSend("/sc/topic/jobs", jobJson);
    });

    lxcService.create(
        lxcCreateDto.getName(),
        lxcCreateDto.getUsername(),
        lxcCreateDto.getPassword(),
        processor
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
      @ApiParam("lxcName") @PathVariable String lxcName,
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
      @ApiParam("lxcName") @PathVariable String lxcName
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
    var containers = lxcService.getAllContainers()
        .stream()
        .map(cont -> modelMapper.map(cont, ContainerDto.class))
        .collect(Collectors.toList());

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
      @ApiParam("lxcName") @PathVariable String lxcName
  ) {
    String res = lxcService.startLxc(lxcName);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  @RequestMapping("/{lxcName}/stop")
  public ResponseEntity<String> stopLxc(
      @ApiParam("lxcName") @PathVariable String lxcName
  ) {
    String res = lxcService.stopLxc(lxcName);
    return new ResponseEntity<>(res, HttpStatus.OK);
  }

  @GetMapping(value = "/{lxcName}/status")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "", response = String.class)
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
      @ApiResponse(code = 403, message = "Access denied"),
      @ApiResponse(code = 500, message = "Expired or invalid JWT token")
  })
  public ResponseEntity<LxcStatusDto> getLxcStatus(@PathVariable String lxcName) {
    LxcStatusDto statusDto = modelMapper.map(lxcService.getLxcStatus(lxcName), LxcStatusDto.class);
    return new ResponseEntity<>(statusDto, HttpStatus.OK);
  }

  @MessageMapping("sc/jobs")
  @SendTo("/sc/topic/jobs")
  public String jobMessage(String msg) {
    log.info("Socket jobMessage: {}", msg);
    return msg;
  }
}
