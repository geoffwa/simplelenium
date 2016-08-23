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

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;

import java.util.regex.Pattern;

class StringPredicates {
  private StringPredicates() {
    // Static class
  }

  public static Predicate<String> isEmpty() {
    return new Predicate<String>() {
      @Override
      public boolean apply(final String value) {
        return value == null || "".equals(value);
      }
    };
  }

  public static Predicate<String> isNull() {
    return Predicates.isNull();
  }

  public static Predicate<String> equalsTo(final String text) {
    return new Predicate<String>() {
      @Override
      public boolean apply(final String value) {
        return value.equals(text);
      }
    };
  }

  public static Predicate<String> contains(final String text) {
    return new Predicate<String>() {
      @Override
      public boolean apply(final String value) {
        return value.contains(text);
      }
    };
  }

  public static Predicate<String> contains(final Pattern regex) {
    return new Predicate<String>() {
      @Override
      public boolean apply(final String value) {
        return regex.matcher(value).find();
      }
    };
  }

  public static Predicate<String> containsWord(String word) {
    final Pattern pattern = Pattern.compile("\\b(" + word + ")\\b");
    return contains(pattern);
  }

  public static Predicate<String> startsWith(final String text) {
    return new Predicate<String>() {
      @Override
      public boolean apply(final String value) {
        return value.startsWith(text);
      }
    };
  }

  public static Predicate<String> endsWith(final String text) {
    return new Predicate<String>() {
      @Override
      public boolean apply(final String value) {
        return value.endsWith(text);
      }
    };
  }

  public static Predicate<String> matches(final Pattern regex) {
    return new Predicate<String>() {
      @Override
      public boolean apply(final String value) {
        return regex.matcher(value).matches();
      }
    };
  }
}
