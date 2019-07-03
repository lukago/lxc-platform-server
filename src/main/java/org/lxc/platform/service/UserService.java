package org.lxc.platform.service;

import java.util.List;
import javax.servlet.http.HttpServletRequest;

import javax.transaction.Transactional;
import org.lxc.platform.model.User;
import org.lxc.platform.dto.PasswordDto;
import org.lxc.platform.dto.UserDto;
import org.lxc.platform.dto.UserUpdateDto;
import org.lxc.platform.exception.HttpException;
import org.lxc.platform.security.JwtTokenProvider;
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

  @Autowired
  public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
      JwtTokenProvider jwtTokenProvider, AuthenticationManager authenticationManager) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtTokenProvider = jwtTokenProvider;
    this.authenticationManager = authenticationManager;
  }

  public String signin(String username, String password) {
    try {
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
      User user = search(username);
      return jwtTokenProvider.createToken(username, user.getRoles());
    } catch (AuthenticationException e) {
      throw new HttpException("Invalid username/password supplied", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Transactional
  public User signup(UserDto user) {
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

      return userRepository.save(userDb);
    } else {
      throw new HttpException("Username is already in use", HttpStatus.UNPROCESSABLE_ENTITY);
    }
  }

  @Transactional
  public void delete(String username) {
    userRepository.deleteByUsername(username);
  }

  public User search(String username) {
    return userRepository
        .findByUsername(username)
        .orElseThrow(() -> new HttpException("The user doesn't exist", HttpStatus.NOT_FOUND));
  }

  public User whoami(HttpServletRequest req) {
    return search(jwtTokenProvider.getUsername(jwtTokenProvider.resolveToken(req)));
  }

  public List<User> getAll() {
    return userRepository.findAll();
  }

  public User update(UserUpdateDto user, String username) {
    User userDb = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new HttpException("Username not found", HttpStatus.NOT_FOUND));

    userDb.setEmail(user.getEmail());
    userDb.setRoles(user.getRoles());
    return userRepository.save(userDb);
  }

  public User updateUserPassword(PasswordDto passwordDto, String username) {
    User userDb = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new HttpException("Username not found", HttpStatus.NOT_FOUND));

    authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
        username, passwordDto.getOldPassword()));

    if (!passwordDto.getPassword().equals(passwordDto.getPasswordRetype())) {
      throw new HttpException("Passwords do not match", HttpStatus.UNPROCESSABLE_ENTITY);
    }

    userDb.setPassword(passwordEncoder.encode(passwordDto.getPassword()));
    return userRepository.save(userDb);
  }
}
