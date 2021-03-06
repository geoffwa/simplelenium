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
package com.github.geoffwa.simplerlenium;

import com.github.geoffwa.simplerlenium.driver.SeleniumDriver;
import com.github.geoffwa.simplerlenium.selectors.ByCssSelectorOrByNameOrById;
import com.google.common.base.Function;
import com.google.common.collect.FluentIterable;
import com.github.geoffwa.simplerlenium.driver.Browser;
import com.github.geoffwa.simplerlenium.filters.LazyDomElement;
import org.openqa.selenium.By;
import org.openqa.selenium.logging.LogEntry;

import java.net.URI;
import java.util.List;

import static java.util.Objects.requireNonNull;

public abstract class Navigation extends DomElementFinder {
  static ThreadLocal<String> baseUrl = new ThreadLocal<>();

  static String getBaseUrl() {
    return baseUrl.get();
  }

  static void setBaseUrl(String url) {
    baseUrl.set(url);
  }

  protected SeleniumDriver driver() {
    return Browser.getCurrentDriver();
  }

  List<String> console() {
    return FluentIterable.from(driver().manage().logs().get("browser").getAll())
        .transform(new Function<LogEntry, String>() {
          @Override
          public String apply(LogEntry logEntry) {
            return logEntry.getMessage().replace(" (undefined:undefined)", "");
          }
        }).toList();
  }

  Object executeJavascript(String javascriptCode, Object... args) {
    return driver().executeScript(javascriptCode, args);
  }

  Navigation goTo(String url) {
    requireNonNull(url, "The url cannot be null");

    if (!URI.create(url.replace(" ", "%20")).isAbsolute()) {
      url = getBaseUrl() + url;
    }

    System.out.println("goTo " + url);

    driver().get(url);

    System.out.println(" - current url " + driver().getCurrentUrl());

    return this;
  }

  Navigation goTo(PageObject page) {
    goTo(page.url());
    return this;
  }

  @Override
  public DomElement find(String selector) {
    return new LazyDomElement(new ByCssSelectorOrByNameOrById(selector));
  }

  @Override
  public DomElement find(By selector) {
    return new LazyDomElement(selector);
  }
}
