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
package net.codestory.simplelenium;

import com.google.common.collect.Lists;
import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.assertj.core.api.Assertions.assertThat;

public class FluentTestTest {
  @Test
  public void parallel() throws Exception {
    final String baseUrl = "http://localhost:" + new TestWebServer().port();

    ExecutorService executorService = Executors.newFixedThreadPool(20);

    List<Future<?>> futures = Lists.newArrayList();

    for (int i = 0; i < 20; i++) {
      futures.add(executorService.submit(new Callable<Void>() {
        @Override
        public Void call() throws Exception {
          new FluentTest(baseUrl)
              .goTo("/")
              .find("h1").should().contain("Hello World").and().not().contain("Unknowm")
              .find("h2").should().contain("SubTitle")
              .find(".age").should().contain("42")
              .goTo("/list")
              .find("li").should().contain("Bob").and().contain("Joe");
          return null;
        }
      }));
    }

    for (Future<?> future : futures) {
      future.get();
    }

  }

  @Test
  public void take_snapshot() {
    TestWebServer testWebServer = new TestWebServer();
    String baseUrl = "http://" + testWebServer.hostname() +  ":" + testWebServer.port();

    new FluentTest(baseUrl).goTo("/").takeSnapshot();

    assertThat(new File("snapshots", "snapshot001.png")).exists();
  }
}
