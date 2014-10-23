/**
 * Copyright (C) 2013-2014 all@code-story.net
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

import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.NotFoundException;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriverException;

import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

import static net.codestory.simplelenium.Retry.Error.KO;
import static net.codestory.simplelenium.Retry.Error.NOT_FOUND;

class Retry {
  private final long timeoutInMs;

  Retry(long duration, TimeUnit timeUnit) {
    this.timeoutInMs = timeUnit.toMillis(duration);
  }

  <T> void execute(Supplier<T> target, Consumer<T> action) {
    WebDriverException lastError = null;

    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        T targetElement = target.get();
        if (targetElement != null) {
          action.accept(targetElement);
          return;
        }
      } catch (WebDriverException e) {
        lastError = e;
      }
    }

    throw lastError;
  }

  <T> boolean verify(Supplier<T> targetSupplier, Predicate<T> predicate) throws NoSuchElementException {
    Error error = KO;

    long start = System.currentTimeMillis();
    while ((System.currentTimeMillis() - start) < timeoutInMs) {
      try {
        if (predicate.test(targetSupplier.get())) {
          return true;
        }

        error = KO;
      } catch (InvalidElementStateException e) {
        error = KO;
      } catch (NotFoundException e) {
        error = NOT_FOUND;
      } catch (StaleElementReferenceException e) {
        // ignore
      }
    }

    if (error == NOT_FOUND) {
      throw new NoSuchElementException("Not found");
    }
    return false;
  }

  static enum Error {
    NOT_FOUND, KO
  }
}
