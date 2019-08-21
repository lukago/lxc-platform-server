package org.lxc.platform.service;

import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import javax.transaction.Transactional;
import org.lxc.platform.dto.UserSafeDto;
import org.lxc.platform.model.Role;
import org.lxc.platform.model.User;
import org.lxc.platform.dto.PasswordDto;
import org.lxc.platform.dto.UserDto;
import org.lxc.platform.dto.UserUpdateDto;
import org.lxc.platform.exception.HttpException;
import org.lxc.platform.security.JwtTokenProvider;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.lxc.platform.respository.UserRepository;

@Service
public class UserService {

  private static final Logger LOG = LoggerFactory.getLogger(UserService.class);

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtTokenProvider jwtTokenProvider;
  private final AuthenticationManager authenticationManager;
  private final ModelMapper modelMapper;
  private final VersionService versionService;

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
          JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager,
          ModelMapper modelMapper, VersionService versionService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
    this.modelMapper = modelMapper;
    this.versionService = versionService;
  }

  public String signin(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      UserSafeDto user = getUser(username);
      return jwtTokenProvider.createToken(username, user.getRoles());
    } catch (AuthenticationException e) {
      throw new HttpException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Transactional
  public UserSafeDto signup(UserDto user) {
    if (!user.getPassword().equals(user.getPasswordRetype())) {
      LOG.info("Passwords do not match");
      throw new HttpException("Passwords do not match", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    if (!userRepository.existsByUsername(user.getUsername())) {
      User userDb = new User();
      userDb.setEmail(user.getEmail());
      userDb.setUsername(user.getUsername());
      userDb.setRoles(user.getRoles());
      userDb.setPassword(passwordEncoder.encode(user.getPassword()));

      return modelMapper.map(userRepository.save(userDb), UserSafeDto.class);
    } else {
      throw new HttpException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Transactional
  public void delete(String username, Long version) {
    versionService.validateVersion(getUserInner(username), version);
    userRepository.deleteByUsername(username);
  }

  public UserSafeDto getUser(String username) {
    return userRepository
        .findByUsername(username)
        .map(user -> modelMapper.map(user, UserSafeDto.class))
        .orElseThrow(() -> new HttpException("The user doesn't exist", HttpStatus.NOT_FOUND));
  }

  public UserSafeDto whoami(HttpServletRequest req) {
    return getUser(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
  }

  public User whoamiInner(HttpServletRequest req) {
    return userRepository
        .findByUsername(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)))
        .orElseThrow(() -> new HttpException("The user doesn't exist", HttpStatus.NOT_FOUND));
  }

  public List<UserSafeDto> getAll() {
    return userRepository.findAll().stream()
        .map(user -> modelMapper.map(user, UserSafeDto.class))
        .collect(Collectors.toList());
  }

  public List<UserSafeDto> getAllAdmins() {
    return userRepository.findAllByRolesContaining(Role.ROLE_ADMIN).stream()
        .map(user -> modelMapper.map(user, UserSafeDto.class))
        .collect(Collectors.toList());
  }

  public UserSafeDto update(UserUpdateDto user, String username, Long version) {
    User userDb = getUserInner(username);
    versionService.validateVersion(userDb, version);

    userDb.setEmail(user.getEmail());
    userDb.setRoles(user.getRoles());
    return modelMapper.map(userRepository.save(userDb), UserSafeDto.class);
  }

  public UserSafeDto updateMe(UserUpdateDto user, User me, Long version) {
    versionService.validateVersion(me, version);
    me.setEmail(user.getEmail());
    return modelMapper.map(userRepository.save(me), UserSafeDto.class);
  }

  public UserSafeDto updateUserPassword(PasswordDto passwordDto, User userDb, Long version) {
    versionService.validateVersion(userDb, version);

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        userDb.getUsername(), passwordDto.getOldPassword()));

    if (!passwordDto.getPassword().equals(passwordDto.getPasswordRetype())) {
      throw new HttpException("Passwords do not match", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    userDb.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
    return modelMapper.map(userRepository.save(userDb), UserSafeDto.class);
  }

  public UserSafeDto updateUserPassword(PasswordDto passwordDto, String username, Long version) {
    User userDb = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new HttpException("Username not found", HttpStatus.NOT_FOUND));

    return updateUserPassword(passwordDto, userDb, version);
  }

  private User getUserInner(String username) {
    return userRepository
            .findByUsername(username)
            .orElseThrow(() -> new HttpException("The user doesn't exist", HttpStatus.NOT_FOUND));
  }
}
