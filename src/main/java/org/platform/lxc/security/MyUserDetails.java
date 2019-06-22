package org.platform.lxc.security;

import org.platform.lxc.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.platform.lxc.respository.UserRepository;

@Service
public class MyUserDetails implements UserDetailsService {

  private static final Logger log = LoggerFactory.getLogger(MyUserDetails.class);

  @Autowired
  private UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    final User user = userRepository
        .findByUsername(username)
        .orElseThrow(() -> new UsernameNotFoundException("User '" + username + "' not found"));

    return org.springframework.security.core.userdetails.User//
        .withUsername(username)
        .password(user.getPassword())
        .authorities(user.getRoles())
        .accountExpired(false)
        .accountLocked(false)
        .credentialsExpired(false)
        .disabled(false)
        .build();
  }
}
