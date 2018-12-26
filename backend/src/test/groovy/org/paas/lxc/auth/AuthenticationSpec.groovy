package org.paas.lxc.auth

import org.springframework.security.test.context.support.WithMockUser
import org.paas.lxc.AbstractMvcSpec

class AuthenticationSpec extends AbstractMvcSpec {

  def "unauthenticated users cannot get resource"() {

  }

  @WithMockUser
  def "authenticated users can get resource"() {

  }
}
