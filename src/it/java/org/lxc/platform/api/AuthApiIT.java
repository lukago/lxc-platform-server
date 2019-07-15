package org.lxc.platform.api;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.gson.Gson;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.lxc.platform.AbstractTest;

class AuthApiIT extends AbstractTest {

  private final HttpClient httpClient = HttpClient.newHttpClient();
  private final Gson gson = new Gson();

  @Test
  void healthCheck() throws Exception {
    HttpRequest request = HttpRequest.newBuilder()
        .uri(URI.create("http://" +
            SERVER_CONTAINER.getServiceHost(SERVER_NAME, SERVER_PORT) +
            ":" +
            SERVER_CONTAINER.getServicePort(SERVER_NAME, SERVER_PORT) +
            "/actuator/health"))
        .GET()
        .build();

    var res = httpClient.send(request, BodyHandlers.ofString());
    assertEquals("UP", gson.fromJson(res.body(), Map.class).get("status"));
  }

}
