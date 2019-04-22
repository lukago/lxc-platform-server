package org.paas.lxc.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.paas.lxc.dto.LxcCreateDto;
import org.paas.lxc.service.LxcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
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

  @PostMapping
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  public ResponseEntity<?> createLxc(@ApiParam("Username") @RequestBody LxcCreateDto lxcCreateDto) {
    EmitterProcessor<String> processor = EmitterProcessor.create();

    processor.subscribe(str -> {
      log.info("LXC: {}", str);
      template.convertAndSend("/sc/topic/jobs", str);
    });

    lxcService.create(lxcCreateDto.getName(), processor);
    return new ResponseEntity<>(HttpStatus.ACCEPTED);
  }

  @MessageMapping("sc/jobs")
  @SendTo("/sc/topic/jobs")
  public String jobMessage(String msg) {
    log.info("Socket jobMessage: {}", msg);
    return msg;
  }
}
