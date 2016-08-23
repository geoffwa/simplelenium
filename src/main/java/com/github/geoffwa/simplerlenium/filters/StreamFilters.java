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
package com.github.geoffwa.simplerlenium.filters;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.openqa.selenium.WebElement;

import java.util.Collections;

class StreamFilters {
  private StreamFilters() {
    // Static class
  }

  private static class SkipLimit implements Function<Iterable<WebElement>, Iterable<WebElement>> {

    private final int skip;
    private final int limit;

    private SkipLimit(int skip, int limit) {
      this.skip = skip;
      this.limit = limit;
    }

    @Override
    public Iterable<WebElement> apply(Iterable<WebElement> elements) {
      if (skip >= 0) {
        elements = Iterables.skip(elements, skip);
      }
      if (limit >= 0) {
        elements = Iterables.limit(elements, limit);
      }
      return elements;
    }
  }

  public static Function<Iterable<WebElement>, Iterable<WebElement>> first() {
    return new SkipLimit(0, 1);
  }

  public static Function<Iterable<WebElement>, Iterable<WebElement>> second() {
    return new SkipLimit(1, 1);
  }

  public static Function<Iterable<WebElement>, Iterable<WebElement>> third() {
    return new SkipLimit(2, 1);
  }

  public static Function<Iterable<WebElement>, Iterable<WebElement>> nth(int index) {
    return new SkipLimit(index - 1, 1);
  }

  public static Function<Iterable<WebElement>, Iterable<WebElement>> limit(final int max) {
    return new Function<Iterable<WebElement>, Iterable<WebElement>>() {
      @Override
      public Iterable<WebElement> apply(Iterable<WebElement> elements) {
        return Iterables.limit(elements, max);
      }
    };
  }

  public static Function<Iterable<WebElement>, Iterable<WebElement>> skip(final int count) {
    return new Function<Iterable<WebElement>, Iterable<WebElement>>() {
      @Override
      public Iterable<WebElement> apply(Iterable<WebElement> elements) {
        return Iterables.skip(elements, count);
      }
    };
  }

  public static Function<Iterable<WebElement>, Iterable<WebElement>> last() {
    return new Function<Iterable<WebElement>, Iterable<WebElement>>() {
      @Override
      public Iterable<WebElement> apply(Iterable<WebElement> elements) {
        WebElement last = Iterables.getLast(elements, null);
        return (last == null) ? Collections.<WebElement>emptySet() : Collections.singleton(last);
      }
    };

  }
}
