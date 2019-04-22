package org.paas.lxc.api

import groovy.json.JsonSlurper
import org.paas.lxc.MockMvcSpec
import org.paas.lxc.dto.CredentialsDto
import org.paas.lxc.dto.SiginResponseDto
import org.paas.lxc.dto.UserDto
import org.paas.lxc.dto.UserSafeDto
import org.paas.lxc.model.Role
import org.paas.lxc.model.User
import org.paas.lxc.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.MediaType
import spock.lang.Shared

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post


class AuthenticationSpec extends MockMvcSpec {

  @Shared
  static SiginResponseDto token

  @Autowired
  UserService userService

  void setup() {
    User user = new User()
    user.setRoles(List.of(Role.ROLE_ADMIN, Role.ROLE_CLIENT))
    user.setEmail("testlxc@google.com")
    user.setUsername("user")
    user.setPassword("password123")

    User user2 = new User()
    user2.setRoles(List.of(Role.ROLE_CLIENT))
    user2.setEmail("testlxc2@google.com")
    user2.setUsername("user2")
    user2.setPassword("password123")

    userService.signup(user)
    userService.signup(user2)
  }

  def 'should return token on authentication'() {
    when:
    def jwtRequest = new CredentialsDto(username: 'user', password: 'password123')
    def response = mockMvc.perform(post('/api/auth/signin')
        .content(toJson(jwtRequest))
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn().response

    def responseMap = new JsonSlurper().parseText(response.contentAsString)
    token = new SiginResponseDto(token: responseMap.token)
    then:
    response.status == 200
    !token.getToken().isEmpty()
  }

  def 'should authorize using token'() {
    when:
    def response = mockMvc.perform(get('/api/users/me')
        .header("Authorization", "Bearer $token.token"))
        .andReturn().response
    def res = new JsonSlurper().parseText(response.contentAsString)
    def user = new UserSafeDto(username: res.username, roles: res.roles, email: res.email)

    then:
    response.status == 200
    user.username == "user"
    user.email == "testlxc@google.com"
    user.roles.contains(Role.ROLE_ADMIN.name())
    user.roles.contains(Role.ROLE_CLIENT.name())
  }

  def 'should have permission'() {
    when:
    def response = mockMvc.perform(post('/api/auth/signup')
        .content(toJson(new UserDto(
        username: "usertest", roles: [Role.ROLE_ADMIN], email: "a2@a.com", password: "password123")))
        .header("Authorization", "Bearer $token.token")
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn().response

    then:
    response.status == 200
  }

  def 'should have no permission'() {
    when:
    def jwtRequest = new CredentialsDto(username: 'user2', password: 'password123')
    def response = mockMvc.perform(post('/api/lxc/create')
        .content(toJson(jwtRequest))
        .contentType(MediaType.APPLICATION_JSON))
        .andReturn().response

    then:
    response.status == 403
  }
}
