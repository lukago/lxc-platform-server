package org.lxc.platform.service;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.transaction.Transactional;
import org.lxc.platform.dto.ContainerDto;
import org.lxc.platform.dto.JobDto;
import org.lxc.platform.dto.ServerInfoDto;
import org.lxc.platform.exception.HttpException;
import org.lxc.platform.model.Container;
import org.lxc.platform.model.JobCode;
import org.lxc.platform.model.LxcStatus;
import org.lxc.platform.model.Role;
import org.lxc.platform.model.User;
import org.lxc.platform.respository.ContainerRepository;
import org.lxc.platform.respository.JobRepository;
import org.lxc.platform.respository.LxcStatusRepository;
import org.lxc.platform.respository.UserRepository;
import org.lxc.platform.service.cmdlib.CmdRunnerFactory;
import org.lxc.platform.service.cmdlib.ICmdConfig;
import org.lxc.platform.service.flow.IProcessor;
import org.lxc.platform.service.job.LxcCreateTask;
import org.lxc.platform.service.job.LxcGenericTask;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LxcService {

  private static final Logger LOG = LoggerFactory.getLogger(LxcService.class);

  private final JobRepository jobRepository;
  private final ContainerRepository containerRepository;
  private final UserRepository userRepository;
  private final LxcStatusRepository lxcStatusRepository;
  private final ICmdConfig cmdConfig;
  private final IProcessor<JobDto> processor;
  private final ModelMapper modelMapper;

  @Autowired
  public LxcService(
      JobRepository jobRepository,
      ContainerRepository containerRepository,
      UserRepository userRepository,
      LxcStatusRepository lxcStatusRepository,
      CmdConfig cmdConfig,
      IProcessor<JobDto> processor,
      ModelMapper modelMapper) {
    this.jobRepository = jobRepository;
    this.containerRepository = containerRepository;
    this.userRepository = userRepository;
    this.lxcStatusRepository = lxcStatusRepository;
    this.cmdConfig = cmdConfig;
    this.processor = processor;
    this.modelMapper = modelMapper;
  }

  @Async
  @Transactional
  public void create(User admin, String name, int port) {
    LOG.info("Creating lxc for {}", name);

    if (!admin.getRoles().contains(Role.ROLE_ADMIN)) {
      throw new HttpException("User is not admin", HttpStatus.UNAUTHORIZED);
    }

    LxcCreateTask lxcTask = new LxcCreateTask(
        CmdRunnerFactory.create(cmdConfig),
        processor,
        jobRepository,
        lxcStatusRepository,
        containerRepository,
        cmdConfig,
        modelMapper,
        name,
        admin,
        port
    );

    lxcTask.run();
  }

  @Async
  @Transactional
  public void getLxcStatus(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    Supplier<LxcStatus> task = () -> {
      var cmd = String.format(cmdConfig.getInfoCmd(), container.getName());
      var statusRes = CmdRunnerFactory.create(cmdConfig).run(cmd);
      var status = new LxcStatus();
      status.setStatusResult(statusRes);
      status.setOwner(container.getOwner());
      status.setName(container.getName());
      status.setPort(container.getPort());
      return status;
    };

    new LxcGenericTask<>(
        processor, jobRepository, lxcStatusRepository, modelMapper,
        "getLxcStatus", JobCode.FETCH, user, task)
        .run();
  }

  @Async
  @Transactional
  public void getLxcStatusAdmin(User admin, String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    if (!admin.getRoles().contains(Role.ROLE_ADMIN)) {
      throw new HttpException("User is not admin", HttpStatus.UNAUTHORIZED);
    }

    Supplier<LxcStatus> task = () -> {
      var cmd = String.format(cmdConfig.getInfoCmd(), container.getName());
      var statusRes = CmdRunnerFactory.create(cmdConfig).run(cmd);
      var status = new LxcStatus();
      status.setStatusResult(statusRes);
      status.setOwner(container.getOwner());
      status.setName(container.getName());
      status.setPort(container.getPort());
      return status;
    };

    new LxcGenericTask<>(
        processor, jobRepository, lxcStatusRepository, modelMapper,
        "getLxcStatus", JobCode.FETCH, admin, task)
        .run();
  }

  @Async
  @Transactional
  public void startLxc(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));


    Supplier<LxcStatus> task = () -> {
      var cmd = String.format(cmdConfig.getStartCmd(), container.getName());
      var res = CmdRunnerFactory.create(cmdConfig).run(cmd);
      var status = new LxcStatus();
      status.setStatusResult(res);
      return status;
    };

    new LxcGenericTask<>(
        processor, jobRepository, lxcStatusRepository, modelMapper,
        "startLxc " + lxcName, JobCode.START, user, task)
        .run();
  }

  @Async
  @Transactional
  public void startLxcAdmin(User admin, String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    if (!admin.getRoles().contains(Role.ROLE_ADMIN)) {
      throw new HttpException("User is not admin", HttpStatus.UNAUTHORIZED);
    }

    Supplier<LxcStatus> task = () -> {
      var cmd = String.format(cmdConfig.getStartCmd(), container.getName());
      var res = CmdRunnerFactory.create(cmdConfig).run(cmd);
      var status = new LxcStatus();
      status.setStatusResult(res);
      return status;
    };

    new LxcGenericTask<>(
        processor, jobRepository, lxcStatusRepository, modelMapper,
        "startLxc " + lxcName, JobCode.START, admin, task)
        .run();
  }

  @Async
  @Transactional
  public void stopLxc(User user, String lxcName) {
    Container container = containerRepository
        .findByNameAndOwner(lxcName, user)
        .orElseThrow(() -> new HttpException("Container for name and user not found", HttpStatus.NOT_FOUND));

    Supplier<LxcStatus> task = () -> {
      var cmd = String.format(cmdConfig.getStopCmd(), container.getName());
      var res = CmdRunnerFactory.create(cmdConfig).run(cmd);
      var status = new LxcStatus();
      status.setStatusResult(res);
      return status;
    };

    new LxcGenericTask<>(
        processor, jobRepository, lxcStatusRepository, modelMapper,
        "stopLxc " + lxcName, JobCode.STOP, user, task)
        .run();
  }

  @Async
  @Transactional
  public void stopLxcAdmin(User admin, String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for name not found", HttpStatus.NOT_FOUND));

    if (!admin.getRoles().contains(Role.ROLE_ADMIN)) {
      throw new HttpException("User is not admin", HttpStatus.UNAUTHORIZED);
    }

    Supplier<LxcStatus> task = () -> {
      var cmd = String.format(cmdConfig.getStopCmd(), container.getName());
      var res = CmdRunnerFactory.create(cmdConfig).run(cmd);
      var status = new LxcStatus();
      status.setStatusResult(res);
      return status;
    };

    new LxcGenericTask<>(
        processor, jobRepository, lxcStatusRepository, modelMapper,
        "stopLxc " + lxcName, JobCode.STOP, admin, task)
        .run();
  }

  @Transactional
  public void assignUserToLxc(String lxcName, String username) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for given name not found", HttpStatus.NOT_FOUND));
    User user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new HttpException("User for given username not found", HttpStatus.NOT_FOUND));

    container.setOwner(user);
    containerRepository.save(container);
  }

  @Transactional
  public void unassignLxcFromUser(String lxcName) {
    Container container = containerRepository
        .findByName(lxcName)
        .orElseThrow(() -> new HttpException("Container for given name not found", HttpStatus.NOT_FOUND));

    container.setOwner(null);
    containerRepository.save(container);
  }

  public List<ContainerDto> getAllContainers() {
    return containerRepository.findAll().stream()
        .map(cont -> modelMapper.map(cont, ContainerDto.class))
        .collect(Collectors.toList());
  }

  public List<ContainerDto> getUserContainers(User user) {
    return containerRepository.findAllByOwner(user).stream()
        .map(cont -> modelMapper.map(cont, ContainerDto.class))
        .collect(Collectors.toList());
  }

  public List<JobDto> getAllJobs(int pageNr) {
    return jobRepository.findAllByOrderByStartDateDesc(PageRequest.of(pageNr, 20)).stream()
        .map(job -> modelMapper.map(job, JobDto.class))
        .collect(Collectors.toList());
  }

  public List<JobDto> getUserJobs(User user, int pageNr) {
    return jobRepository.findAllByCreatedByOrderByStartDateDesc(user, PageRequest.of(pageNr, 20))
        .stream()
        .map(job -> modelMapper.map(job, JobDto.class))
        .collect(Collectors.toList());
  }

  public ServerInfoDto getServerInfo() {
    ServerInfoDto serverInfoDto = new ServerInfoDto();
    serverInfoDto.setIp(cmdConfig.getServerIp());
    serverInfoDto.setPort(cmdConfig.getServerPort());
    return serverInfoDto;
  }
}
