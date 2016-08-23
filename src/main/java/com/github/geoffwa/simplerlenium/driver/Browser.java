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
package com.github.geoffwa.simplerlenium.driver;

import com.github.geoffwa.simplerlenium.driver.chrome.ChromeDriverDownloader;
import com.github.geoffwa.simplerlenium.driver.phantomjs.PhantomJsDownloader;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;
import org.openqa.selenium.Capabilities;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;

public enum Browser {
  PHANTOM_JS(new Function<Capabilities, RemoteWebDriver>() {
    @Override
    public RemoteWebDriver apply(Capabilities capabilities) {
      return new PhantomJsDownloader().createNewDriver(capabilities);
    }
  }),
  CHROME(new Function<Capabilities, RemoteWebDriver>() {
    @Override
    public RemoteWebDriver apply(Capabilities capabilities) {
      return new ChromeDriverDownloader().createNewDriver(capabilities);
    }
  }),
  FIREFOX(new Function<Capabilities, RemoteWebDriver>() {
    @Override
    public RemoteWebDriver apply(Capabilities capabilities) {
      return new FirefoxDriver(capabilities);
    }
  });

  private final Function<Capabilities, RemoteWebDriver> driverSupplier;
  private final ThreadLocal<SeleniumDriver> perThreadDriver = new ThreadLocal<SeleniumDriver>() {
    @Override
    protected SeleniumDriver initialValue() {
      Capabilities capabilities = getDesiredCapabilities();
      RemoteWebDriver webDriver = driverSupplier.apply(capabilities);

      return ThreadSafeDriver.makeThreadSafe(webDriver);
    }
  };
  private Capabilities desiredCapabilities;

  Browser(Function<Capabilities, RemoteWebDriver> driverSupplier) {
    this.driverSupplier = driverSupplier;
  }

  public void setDesiredCapabilities(Capabilities desiredCapabilities) {
    this.desiredCapabilities = desiredCapabilities;
  }

  public Capabilities getDesiredCapabilities() {
    return desiredCapabilities;
  }

  public SeleniumDriver getDriverForThread() {
    return perThreadDriver.get();
  }

  public static Browser getCurrentBrowser() {
    final String browserName = Configuration.BROWSER.get();

    Browser browser = FluentIterable.of(Browser.values())
        .firstMatch(new Predicate<Browser>() {
          @Override
          public boolean apply(Browser browser) {
            return browser.name().equalsIgnoreCase(browserName);
          }
        }).orNull();

    if (browser == null)
      throw new IllegalStateException("No selenium driver for " + browserName);

    return browser;
  }

  public static SeleniumDriver getCurrentDriver() {
    return getCurrentBrowser().getDriverForThread();
  }
}
