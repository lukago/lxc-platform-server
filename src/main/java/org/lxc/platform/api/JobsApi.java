package org.lxc.platform.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.lxc.platform.dto.JobDto;
import org.lxc.platform.service.LxcService;
import org.lxc.platform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/jobs")
@Api(tags = "auth")
public class JobsApi {
  private final LxcService lxcService;
  private final UserService userService;

  @Autowired
  public JobsApi(LxcService lxcService, UserService userService) {
    this.lxcService = lxcService;
    this.userService = userService;
  }

  @GetMapping("/{pageNr}")
  @PreAuthorize("hasRole('ROLE_ADMIN')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  public ResponseEntity<List<JobDto>> getJobs(@PathVariable int pageNr) {
    var containers = lxcService.getAllJobs(pageNr);
    return new ResponseEntity<>(containers, HttpStatus.OK);
  }

  @GetMapping("/me/{pageNr}")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_CLIENT')")
  @ApiOperation(value = "")
  @ApiResponses(value = {
      @ApiResponse(code = 400, message = "Something went wrong"),
  })
  public ResponseEntity<List<JobDto>> getJobsForUser(HttpServletRequest req,
      @PathVariable int pageNr) {
    var containers = lxcService.getUserJobs(userService.whoamiInner(req), pageNr);
    return new ResponseEntity<>(containers, HttpStatus.OK);
  }
}
