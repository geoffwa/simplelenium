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

import com.github.geoffwa.simplerlenium.DomElement;
import com.github.geoffwa.simplerlenium.ShouldChain;
import com.github.geoffwa.simplerlenium.selectors.ByCssSelectorOrByNameOrById;
import com.github.geoffwa.simplerlenium.text.Text;
import com.google.common.base.*;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebElement;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

class LazyShould extends ShouldChain {
  private final LazyDomElement element;
  private final Retry retry;
  private final boolean ok;

  LazyShould(LazyDomElement element, Retry retry, boolean ok) {
    this.element = element;
    this.retry = retry;
    this.ok = ok;
  }

  // Nested find

  public DomElement find(String selector) {
    if (element.parent() != null) {
      return element.parent().find(selector);
    }
    return new LazyDomElement(new ByCssSelectorOrByNameOrById(selector));
  }

  public DomElement find(By selector) {
    if (element.parent() != null) {
      return element.parent().find(selector);
    }
    return new LazyDomElement(selector);
  }

  // Modifiers

  @Override
  public LazyShould within(long duration, TimeUnit timeUnit) {
    return new LazyShould(element, new Retry(duration, timeUnit), ok);
  }

  @Override
  public LazyShould not() {
    return new LazyShould(element, retry, !ok);
  }

  @Override
  public LazyShould and() {
    return this; // For nicer fluent api
  }

  @Override
  public LazyShould should() {
    return this; // For nicer fluent api
  }

  // Expectations

  @Override
  public LazyShould contain(final String... texts) {
    return verify(
        doesOrNot("contain") + " (" + Joiner.on(';').join(texts) + ")",
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(final List<WebElement> webElements) {
            return FluentIterable.of(texts).allMatch(
                new Predicate<String>() {
                  @Override
                  public boolean apply(final String expected) {
                    return FluentIterable.from(webElements).anyMatch(WebElementHelper.CONTAINS_TEXT(expected));
                  }
                }
            );
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It contains " + statuses(webElements, WebElementHelper.TEXT);
          }
        });
  }

  @Override
  public LazyShould beEmpty() {
    return verify(
        isOrNot("empty"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).allMatch(new Predicate<WebElement>() {
              @Override
              public boolean apply(WebElement webElement) {
                return WebElementHelper.text(webElement).isEmpty();
              }
            });
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It contains " + statuses(webElements, WebElementHelper.TEXT);
          }
        });
  }

  @Override
  public LazyShould match(final Pattern regexp) {
    return verify(
        doesOrNot("match") + " (" + regexp.pattern() + ")",
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).anyMatch(new Predicate<WebElement>() {
              @Override
              public boolean apply(WebElement webElement) {
                return regexp.matcher(WebElementHelper.text(webElement)).matches();
              }
            });
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It contains " + statuses(webElements, WebElementHelper.TEXT);
          }
        });
  }

  @Override
  public LazyShould beEnabled() {
    return verify(
        isOrNot("enabled"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).allMatch(new Predicate<WebElement>() {
              @Override
              public boolean apply(WebElement webElement) {
                return webElement.isEnabled();
              }
            });
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It is " + statuses(webElements, new Function<WebElement, String>() {
              @Override
              public String apply(WebElement element) {
                return enabledStatus(element);
              }
            });
          }
        });
  }

  @Override
  public LazyShould beDisplayed() {
    return verify(
        isOrNot("displayed"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).allMatch(ElementPredicates.IS_DISPLAYED);
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It is " + statuses(webElements, new Function<WebElement, String>() {
                  @Override
                  public String apply(WebElement webElement) {
                    return displayedStatus(webElement);
                  }
                });
          }
        });
  }

  @Override
  public LazyShould beSelected() {
    return verify(
        isOrNot("selected"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).allMatch(ElementPredicates.IS_SELECTED);
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It is " + statuses(webElements, new Function<WebElement, String>() {
              @Override
              public String apply(WebElement webElement) {
                return selectedStatus(webElement);
              }
            });
          }
        });
  }

  @Override
  public LazyShould haveLessItemsThan(final int maxCount) {
    return verify(
        doesOrNot("contain") + " less than " + Text.plural(maxCount, "element"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return webElements.size() < maxCount;
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It contains " + Text.plural(webElements.size(), "element");
          }
        });
  }

  @Override
  public LazyShould haveSize(final int size) {
    return verify(
        doesOrNot("contain") + " " + Text.plural(size, "element"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return webElements.size() == size;
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It contains " + Text.plural(webElements.size(), "element");
          }
        });
  }

  @Override
  public LazyShould haveMoreItemsThan(final int minCount) {
    return verify(
        doesOrNot("contain") + " more than " + Text.plural(minCount, "element"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return webElements.size() > minCount;
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It contains " + Text.plural(webElements.size(), "element");
          }
        });
  }

  @Override
  public LazyShould exist() {
    return verify(
        doesOrNot("exist"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty();
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It contains " + Text.plural(webElements.size(), "element");
          }
        });
  }

  @Override
  public LazyShould haveDimension(final int width, final int height) {
    return verify(
        hasOrNot("dimension"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).allMatch(HAS_DIMENSION(width, height));
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It measures " + statuses(webElements, ElementFunctions.DIMENSION);
          }
        });
  }

  @Override
  public LazyShould beAtLocation(final int x, final int y) {
    return verify(
        isOrNot("at location"),
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).allMatch(ElementPredicates.HAS_LOCATION(x, y));
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> webElements) {
            return "It is at location " + statuses(webElements, ElementFunctions.LOCATION);
          }
        });
  }

  @Override
  public LazyShould match(final Predicate<WebElement> condition) {

    return verify(
        doesOrNot("match") + " (" + condition + ")",
        new Predicate<List<WebElement>>() {
          @Override
          public boolean apply(List<WebElement> webElements) {
            return !webElements.isEmpty() && FluentIterable.from(webElements).allMatch(condition);
          }
        },
        new Function<List<WebElement>, String>() {
          @Override
          public String apply(List<WebElement> elements) {
            return "It is  " + statuses(elements, new Function<WebElement, String>() {
              @Override
              public String apply(WebElement webElement) {
                return Boolean.toString(condition.apply(webElement));
              }
            });
          }
        });
  }

  private LazyShould verify(String message, Predicate<List<WebElement>> predicate, Function<List<WebElement>, String> toErrorMessage) {
    String verification = "verify that " + element + " " + message;
    System.out.println("   -> " + verification);

    try {
      if (!retry.verify(lazyFindElements(), ok ? predicate : Predicates.not(predicate))) {
        throw Failure.create("Failed to " + verification + ". " + toErrorMessage.apply(findElements()));
      }
    } catch (NoSuchElementException e) {
      throw Failure.create("Element not found. Failed to " + verification);
    }

    return ok ? this : not();
  }

  // Internal

  private List<WebElement> findElements() {
    return ImmutableList.copyOf(element.iterable());
  }

  private Supplier<List<WebElement>> lazyFindElements() {
    return new Supplier<List<WebElement>>() {
      @Override
      public List<WebElement> get() {
        return findElements();
      }
    };
  }

  private String doesOrNot(String verb) {
    return Text.doesOrNot(!ok, verb);
  }

  private String isOrNot(String state) {
    return Text.isOrNot(!ok, state);
  }

  private String hasOrNot(String what) {
    return Text.hasOrNot(!ok, what);
  }

  private static Predicate<WebElement> HAS_DIMENSION(final int width, final int height) {
    return new Predicate<WebElement>() {
      @Override
      public boolean apply(WebElement webElement) {
        Dimension dimension = webElement.getSize();
        return dimension.getWidth() == width && dimension.getHeight() == height;
      }
    };
  }

  private static String statuses(List<WebElement> elements, final Function<WebElement, String> toStatus) {
    return "(" + FluentIterable.from(elements)
        .transform(toStatus)
        .join(Joiner.on(';')) + ")";
  }

  private static String enabledStatus(WebElement element) {
    return element.isEnabled() ? "enabled" : "not enabled";
  }

  private static String displayedStatus(WebElement element) {
    return element.isDisplayed() ? "displayed" : "not displayed";
  }

  private static String selectedStatus(WebElement element) {
    if (!ElementPredicates.IS_SELECTABLE.apply(element)) {
      return "not selectable";
    }

    return element.isSelected() ? "selected" : "not selected";
  }

}
