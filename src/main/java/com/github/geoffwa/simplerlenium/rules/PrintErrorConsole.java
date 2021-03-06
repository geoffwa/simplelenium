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
package com.github.geoffwa.simplerlenium.rules;

import com.github.geoffwa.simplerlenium.driver.Browser;
import org.junit.rules.TestWatcher;
import org.junit.runner.Description;
import org.openqa.selenium.logging.LogEntry;

import java.util.List;

public class PrintErrorConsole extends TestWatcher {
  @Override
  protected void failed(Throwable e, Description description) {
    List<LogEntry> logs = Browser.getCurrentDriver().manage().logs().get("browser").getAll();

    System.err.println("Browser's console:");
    if (logs.isEmpty()) {
      System.err.println("<EMPTY>");
    } else {
      for (LogEntry log : logs) {
        System.err.println(" - " + log.getMessage().replace(" (undefined:undefined)", ""));
      }
    }
  }
}
