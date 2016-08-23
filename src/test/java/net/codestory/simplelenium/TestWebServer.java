/**
 * Copyright (C) 2013-2015 all@code-story.net
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
/**
 * NOTE: modified from original
 */
package net.codestory.simplelenium;

import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class TestWebServer {

  private final static HttpServer serverStartedOnlyOnce = createServer();

  public String hostname() {
    return serverStartedOnlyOnce.getAddress().getHostName();
  }

  public int port() {
    return serverStartedOnlyOnce.getAddress().getPort();
  }

  private static HttpServer createServer() {
    try {
      HttpServer server = HttpServer.create(new InetSocketAddress("localhost", 0), 1024);
      registerHandlers(server);
      server.setExecutor(null);
      server.start();
      return server;
    } catch (IOException e) {
      throw new IllegalStateException("failed to start http server on localhost:0", e);
    }
  }

  private static abstract class TextHtmlHandler implements HttpHandler {
    private static final byte[] NOT_FOUND
        = "<h1>Page not found</h1>".getBytes(StandardCharsets.UTF_8);

    private final String path;

    protected TextHtmlHandler(String path) {
      this.path = path;
    }

    HttpContext register(HttpServer server) {
      return server.createContext(path, this);
    }

    protected void send404Response(HttpExchange httpExchange) throws IOException {
      sendResponse(httpExchange, 404, NOT_FOUND);
    }

    protected void sendResponse(HttpExchange httpExchange, int status, byte[] responseBytes) throws IOException {
      httpExchange.sendResponseHeaders(status, responseBytes.length);
      try (OutputStream bodyOutput = httpExchange.getResponseBody()) {
        bodyOutput.write(responseBytes);
      }
    }

    protected boolean matchesPath(URI requestURI) {
      return requestURI.getPath().equals(path);
    }

    protected void setContentTypeHeader(HttpExchange httpExchange) {
      Headers responseHeaders = httpExchange.getResponseHeaders();
      responseHeaders.add("Content-Type", "text/html; charset=utf-8");
    }
  }

  private static class ResourceTextHtmlHandler extends TextHtmlHandler {

    private final String resource;

    public ResourceTextHtmlHandler(String path, String resource) {
      super(path);
      this.resource = resource;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
      setContentTypeHeader(httpExchange);

      URI requestURI = httpExchange.getRequestURI();
      if (!matchesPath(requestURI)) {
        send404Response(httpExchange);
        return;
      }

      try (InputStream resourceAsStream = getClass().getClassLoader().getResourceAsStream(resource)) {
        if (resourceAsStream == null) {
          send404Response(httpExchange);
          return;
        }
        byte[] responseBytes = ByteStreams.toByteArray(resourceAsStream);
        sendResponse(httpExchange, 200, responseBytes);
      }
    }
  }

  private static class StringTextHtmlHandler extends TextHtmlHandler {

    private final byte[] responseBytes;

    StringTextHtmlHandler(String path, String responseText) {
      super(path);
      this.responseBytes = responseText.getBytes(StandardCharsets.UTF_8);
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
      setContentTypeHeader(httpExchange);

      URI requestURI = httpExchange.getRequestURI();
      if (!matchesPath(requestURI)) {
        send404Response(httpExchange);
      } else {
        sendResponse(httpExchange, 200, responseBytes);
      }
    }

  }

  private static void registerHandlers(HttpServer server) {
    new StringTextHtmlHandler("/", "<h1>Hello World</h1>" +
        "<h2>SubTitle</h2>" +
        "<div id='name'>Bob</div>" +
        "<div class='age'>42</div>" +
        "<span name='qualifiers[]'></span>" +
        "<ul>" +
        "   <li><em class='item'>italic</em></li>" +
        "</ul>" +
        "<a href='/'>First Link</a>" +
        "<a href='/list'>Second Link</a>").register(server);

    new StringTextHtmlHandler("/list",
      "<h1>Hello World</h1>" +
      "<ul>" +
        "   <li id='bob' name='theBob' class='name man cartoon'>Bob Morane</li>" +
        "   <li id='joe' name='theJoe' class='name man tv'>Joe l'Indien</li>" +
      "</ul>"
    ).register(server);

    new StringTextHtmlHandler("/form",
      "<input id=\"name\" name=\"name\" type='text' value='The Name'>" +
      "<input id=\"city\" name=\"city\" type='text' value='The City'>" +
      "<input id=\"the_field_with_a_long_name\" type='text' value=''>" +
      "<input id=\"the.field.with.a.long.name\" type='text' value=''>"
    ).register(server);

    new StringTextHtmlHandler("/nested",
      "<div id='first'><div class='child'>First Child</div></div>" +
      "<div id='second'><div class='child'>Second Child</div></div>" +
      "<div id='third'>" +
        "<div class='first_child'>First Child</div>" +
        "<div class='second_child'>Second Child</div>" +
      "</div>"
    ).register(server);

    new StringTextHtmlHandler("/error",
      "<script>undefined.unknown() ;</script>"
    ).register(server);

    new StringTextHtmlHandler("/select",
      "<select>" +
        "<option value=\"1\">FIRST</option>" +
        "<option value=\"2\" selected>SECOND</option>" +
        "<option value=\"3\">THIRD</option>" +
      "</select>"
    ).register(server);

    new ResourceTextHtmlHandler("/dnd", "dragAndDrop/dnd.html")
        .register(server);
    new ResourceTextHtmlHandler("/angular.min.js", "dragAndDrop/angular.min.js")
        .register(server);
    new ResourceTextHtmlHandler("/app.js", "dragAndDrop/app.js")
        .register(server);
    new ResourceTextHtmlHandler("/ngDraggable.js", "dragAndDrop/ngDraggable.js")
        .register(server);
    new ResourceTextHtmlHandler("/style.css", "dragAndDrop/style.css")
        .register(server);
  }
}
