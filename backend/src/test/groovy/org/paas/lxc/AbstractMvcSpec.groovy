package org.paas.lxc

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootContextLoader
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import spockmvc.SpockMvc

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity

@ContextConfiguration(
  loader = SpringBootContextLoader,
  classes = [BootReactApplication]
)
@ActiveProfiles("test")
abstract class AbstractMvcSpec {

  @Delegate
  private SpockMvc spockMvc

  @Autowired
  private WebApplicationContext wac

  def setup() {
    spockMvc = new SpockMvc(buildMockMvc(wac))
  }

  MockMvc buildMockMvc(WebApplicationContext wac) {
    MockMvcBuilders
      .webAppContextSetup(wac)
      .apply(springSecurity())
      .build()
  }
}
