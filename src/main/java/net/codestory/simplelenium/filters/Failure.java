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
package net.codestory.simplelenium.filters;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

class Failure {
  private static final String PREFIX = Failure.class.getPackage().getName() + ".";

  private static final Predicate<StackTraceElement> CLASS_NAME_NOT_CONTAINS_PREFIX = new Predicate<StackTraceElement>() {
    @Override
    public boolean apply(StackTraceElement element) {
      return !element.getClassName().contains(PREFIX);
    }
  };

  private Failure() {
    // Static class
  }

  public static AssertionError create(String message) {
    AssertionError error = new AssertionError(message);
    removeSimpleleniumFromStackTrace(error);
    return error;
  }

  private static void removeSimpleleniumFromStackTrace(Throwable throwable) {
    StackTraceElement[] stackTrace = throwable.getStackTrace();
    StackTraceElement[] filtered = FluentIterable.of(stackTrace)
            .filter(CLASS_NAME_NOT_CONTAINS_PREFIX)
            .toArray(StackTraceElement.class);
    throwable.setStackTrace(filtered);
  }
}
