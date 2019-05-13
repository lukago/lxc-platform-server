package org.paas.lxc.service;

import javax.servlet.http.HttpServletRequest;

import javax.transaction.Transactional;
import org.paas.lxc.exception.HttpException;
import org.paas.lxc.model.User;
import org.paas.lxc.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.paas.lxc.respository.UserRepository;

@Service
public class UserService {

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  @Autowired
  private JwtTokenProvider jwtTokenProvider;

  @Autowired
  private AuthenticationManager authenticationManager;

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
  public User signup(User user) {
    if (!userRepository.existsByUsername(user.getUsername())) {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      return userRepository.save(user);
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

}
