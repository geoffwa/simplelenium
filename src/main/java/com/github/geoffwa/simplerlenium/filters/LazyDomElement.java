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

import com.github.geoffwa.simplerlenium.BiConsumer;
import com.github.geoffwa.simplerlenium.Consumer;
import com.github.geoffwa.simplerlenium.DomElement;
import com.github.geoffwa.simplerlenium.Should;
import com.github.geoffwa.simplerlenium.selectors.ByCssSelectorOrByNameOrById;
import com.github.geoffwa.simplerlenium.text.Text;
import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.TimeUnit;

public class LazyDomElement extends DomElement {
  private static final Function<WebElement, String> GET_ELEMENT_TEXT = new Function<WebElement, String>() {
    @Override
    public String apply(WebElement webElement) {
      return WebElementHelper.text(webElement);
    }
  };
  private static final Function<WebElement, String> GET_ELEMENT_ID_ATTR = new Function<WebElement, String>() {
    @Override
    public String apply(WebElement webElement) {
      return webElement.getAttribute("id");
    }
  };
  private static final Function<WebElement, String> GET_ELEMENT_NAME_ATTR = new Function<WebElement, String>() {
    @Override
    public String apply(WebElement webElement) {
      return webElement.getAttribute("name");
    }
  };
  private static final Function<WebElement, String> GET_ELEMENT_TAG_NAME = new Function<WebElement, String>() {
    @Override
    public String apply(WebElement webElement) {
      return webElement.getTagName();
    }
  };
  private static final Function<WebElement, String> GET_ELEMENT_CLASS_ATTR = new Function<WebElement, String>() {
    @Override
    public String apply(WebElement webElement) {
      return webElement.getAttribute("class");
    }
  };

  private final LazyDomElement parent;
  private final By selector;
  private final ElementFilter filter;
  private final Retry retry;

  public LazyDomElement(By selector) {
    this(selector, Retry._30_SECONDS);
  }

  public LazyDomElement(By selector, Retry retry) {
    this(null, selector, ElementFilter.any(), retry);
  }

  public LazyDomElement(LazyDomElement parent, By selector) {
    this(parent, selector, Retry._30_SECONDS);
  }

  public LazyDomElement(LazyDomElement parent, By selector, Retry retry) {
    this(parent, selector, ElementFilter.any(), retry);
  }

  private LazyDomElement(LazyDomElement parent, By selector, ElementFilter filter, Retry retry) {
    this.parent = parent;
    this.selector = selector;
    this.filter = filter;
    this.retry = retry;
  }

  // Nested find

  @Override
  public DomElement find(String selector) {
    return new LazyDomElement(this, new ByCssSelectorOrByNameOrById(selector));
  }

  @Override
  public DomElement find(By selector) {
    return new LazyDomElement(this, selector);
  }

  // Narrow find

  @Override
  public ElementFilterBuilder withText() {
    return with("text", GET_ELEMENT_TEXT);
  }

  @Override
  public ElementFilterBuilder withId() {
    return with("id", GET_ELEMENT_ID_ATTR);
  }

  @Override
  public ElementFilterBuilder withName() {
    return with("name", GET_ELEMENT_NAME_ATTR);
  }

  @Override
  public ElementFilterBuilder withTagName() {
    return with("tag name", GET_ELEMENT_TAG_NAME);
  }

  @Override
  public ElementFilterBuilder withClass() {
    return with("class", GET_ELEMENT_CLASS_ATTR);
  }

  @Override
  public ElementFilterBuilder with(final String name) {
    Function<WebElement, String> toValue = new Function<WebElement, String>() {
      @Override
      public String apply(WebElement webElement) {
        return webElement.getAttribute(name);
      }
    };
    return with("attribute[" + name + "]", toValue);
  }

  @Override
  public ElementFilterBuilder withCss(final String name) {
    Function<WebElement, String> toValue = new Function<WebElement, String>() {
      @Override
      public String apply(WebElement webElement) {
        return webElement.getCssValue(name);
      }
    };
    return with("cssValue[" + name + "]", toValue);
  }

  @Override
  public ElementFilterBuilder with(String description, Function<WebElement, String> toValue) {
    return new ElementFilterBuilder(this, description, toValue, true);
  }

  // Limit results

  @Override
  public LazyDomElement first() {
    return filter("first", StreamFilters.first());
  }

  @Override
  public LazyDomElement second() {
    return filter("second", StreamFilters.second());
  }

  @Override
  public LazyDomElement third() {
    return filter("third", StreamFilters.third());
  }

  @Override
  public LazyDomElement nth(int index) {
    return filter("nth[" + index + "]", StreamFilters.nth(index));
  }

  @Override
  public LazyDomElement limit(int max) {
    return filter("limit[" + max + "]", StreamFilters.limit(max));
  }

  @Override
  public LazyDomElement skip(int count) {
    return filter("skip[" + count + "]", StreamFilters.skip(count));
  }

  @Override
  public LazyDomElement last() {
    return filter("last", StreamFilters.last());
  }

  @Override
  public LazyDomElement filter(String description,
                               Function<Iterable<WebElement>, Iterable<WebElement>> filter) {
    return with(new ElementFilter(", " + description, filter));
  }

  LazyDomElement with(ElementFilter filter) {
    return new LazyDomElement(parent, selector, this.filter.and(filter), retry);
  }

  // Assertions

  @Override
  public Should should() {
    return new LazyShould(this, Retry._30_SECONDS, true);
  }

  // Actions

  @Override
  public LazyDomElement fill(final CharSequence text) {
    return execute("fill(" + text + ")", new Consumer<WebElement>() {
      @Override
      public void accept(WebElement webElement) {
        webElement.clear();
        webElement.sendKeys(text);
      }
    });
  }

  @Override
  public LazyDomElement append(final CharSequence text) {
    return execute("append(" + text + ")",
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            webElement.sendKeys(text);
          }
        });
  }

  @Override
  public LazyDomElement pressReturn() {
    return execute("pressReturn()",
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            webElement.sendKeys(Keys.RETURN);
          }
        });
  }

  @Override
  public LazyDomElement pressEnter() {
    return execute("pressEnter()",
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            webElement.sendKeys(Keys.ENTER);
          }
        });
  }

  @Override
  public LazyDomElement sendKeys(final CharSequence... keysToSend) {
    return execute("sendKeys()",
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            webElement.sendKeys(keysToSend);
          }
        });
  }

  @Override
  public LazyDomElement clear() {
    return execute("clear()",
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            webElement.clear();
          }
        });
  }

  @Override
  public LazyDomElement submit() {
    return execute("submit",
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            webElement.submit();
          }
        });
  }

  @Override
  public LazyDomElement click() {
    return execute("click",
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            webElement.click();
          }
        });
  }

  @Override
  public LazyDomElement check() {
    return execute("check", new Consumer<WebElement>() {
      @Override
      public void accept(WebElement webElement) {
        if (!webElement.isSelected())
          webElement.click();
      }
    });
  }

  @Override
  public LazyDomElement uncheck() {
    return execute("uncheck", new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            if (webElement.isSelected())
              webElement.click();
          }
        });
  }

  @Override
  public LazyDomElement click(final int x, final int y) {
    return executeActions("click(" + x + "," + y + ")",
        new BiConsumer<WebElement, Actions>() {
          @Override
          public void accept(WebElement webElement, Actions actions) {
            actions.moveToElement(webElement, x, y).click();
          }
        });
  }

  @Override
  public LazyDomElement doubleClick() {
    return executeActions("doubleClick",
        new BiConsumer<WebElement, Actions>() {
          @Override
          public void accept(WebElement webElement, Actions actions) {
            actions.doubleClick(webElement);
          }
        });
  }

  @Override
  public LazyDomElement doubleClick(final int x, final int y) {
    return executeActions("doubleClick(" + x + "," + y + ")",
        new BiConsumer<WebElement, Actions>() {
          @Override
          public void accept(WebElement webElement, Actions actions) {
            actions.moveToElement(webElement, x, y).doubleClick();
          }
        });
  }

  @Override
  public LazyDomElement clickAndHold() {
    return executeActions("clickAndHold", new BiConsumer<WebElement, Actions>() {
      @Override
      public void accept(WebElement webElement, Actions actions) {
        actions.clickAndHold(webElement);
      }
    });
  }

  @Override
  public LazyDomElement dragAndDropTo(final String destinationSelector) {
    return executeActions("dragAndDropTo(" + destinationSelector + ")", new BiConsumer<WebElement, Actions>() {
      @Override
      public void accept(WebElement webElement, Actions actions) {
        actions.clickAndHold(webElement)
            .pause(100)
            .release(driver().findElementByCssSelector(destinationSelector));
      }
    });
  }

  @Override
  public LazyDomElement contextClick() {
    return executeActions("contextClick", new BiConsumer<WebElement, Actions>() {
      @Override
      public void accept(WebElement webElement, Actions actions) {
        actions.contextClick(webElement);
      }
    });
  }

  @Override
  public LazyDomElement release() {
    return executeActions("release", new BiConsumer<WebElement, Actions>() {
      @Override
      public void accept(WebElement webElement, Actions actions) {
        actions.release(webElement);
      }
    });
  }

  @Override
  public LazyDomElement executeActions(String description, final BiConsumer<WebElement, Actions> actionsOnElement) {
    return execute(description, new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            Actions actions = new Actions(driver());
            actionsOnElement.accept(webElement, actions);
            actions.build().perform();
          }
        });
  }

  // Selection

  @Override
  public LazyDomElement select(final String text) {
    return executeSelect("select(" + text + ")",
        new Consumer<Select>() {
          @Override
          public void accept(Select select) {
            select.selectByVisibleText(text);
          }
        });
  }

  @Override
  public LazyDomElement deselect() {
    return executeSelect("deselect()",
        new Consumer<Select>() {
          @Override
          public void accept(Select select) {
            select.deselectAll();
          }
        });
  }

  @Override
  public LazyDomElement deselectByValue(final String value) {
    return executeSelect("deselectByValue(" + value + ")",
        new Consumer<Select>() {
          @Override
          public void accept(Select select) {
            select.deselectByValue(value);
          }
        });
  }

  @Override
  public LazyDomElement deselectByVisibleText(final String text) {
    return executeSelect("deselectByVisibleText(" + text + ")",
        new Consumer<Select>() {
          @Override
          public void accept(Select select) {
            select.deselectByVisibleText(text);
          }
        });
  }

  @Override
  public LazyDomElement deselectByIndex(final int index) {
    return executeSelect("deselectByIndex(" + index + ")",
        new Consumer<Select>() {
          @Override
          public void accept(Select select) {
            select.deselectByIndex(index);
          }
        });
  }

  @Override
  public LazyDomElement selectByIndex(final int index) {
    return executeSelect("selectByIndex(" + index + ")",
        new Consumer<Select>() {
          @Override
          public void accept(Select select) {
            select.selectByIndex(index);
          }
        });
  }

  @Override
  public LazyDomElement selectByValue(final String value) {
    return executeSelect("selectByValue(" + value + ")",
        new Consumer<Select>() {
          @Override
          public void accept(Select select) {
            select.selectByValue(value);
          }
        });
  }

  @Override
  public LazyDomElement executeSelect(String description, final Consumer<Select> selectOnElement) {
    return execute(description,
        new Consumer<WebElement>() {
          @Override
          public void accept(WebElement webElement) {
            selectOnElement.accept(new Select(webElement));
          }
        });
  }

  // Actions on low level elements

  @Override
  public LazyDomElement execute(Consumer<WebElement> action) {
    return execute("execute(" + action + ")", action);
  }

  // Retry

  @Override
  public LazyDomElement retryFor(long duration, TimeUnit timeUnit) {
    return new LazyDomElement(parent, selector, this.filter.and(filter), new Retry(duration, timeUnit));
  }

  // Internal

  private LazyDomElement execute(String message, Consumer<WebElement> action) {
    System.out.println(" - " + Text.toString(selector) + filter.getDescription() + "." + message);

    Supplier<Optional<WebElement>> findOne = new Supplier<Optional<WebElement>>() {
      @Override
      public Optional<WebElement> get() {
        return Optional.fromNullable(Iterables.getFirst(iterable(), null));
      }
    };

    try {
      retry.execute(findOne, action);
    } catch (NoSuchElementException e) {
      throw new AssertionError("Element not found: " + Text.toString(selector));
    }

    // After an action, go back to root level for future finds
    return new LazyDomElement(parent, selector, filter, retry) {
      public DomElement find(String selector) {
        return new LazyDomElement(new ByCssSelectorOrByNameOrById(selector));
      }

      public DomElement find(By selector) {
        return new LazyDomElement(selector);
      }
    };
  }

  @Override
  public String toString() {
    return ((parent == null) ? "" : parent.toString() + " ") + Text.toString(selector) + filter.getDescription();
  }

  LazyDomElement parent() {
    return parent;
  }

  Iterable<WebElement> iterable() {
    Iterator<WebElement> webElements;
    if (parent != null) {
      webElements = FluentIterable.from(parent.iterable())
          .transformAndConcat(new Function<WebElement, Iterable<WebElement>>() {
            @Override
            public Iterable<WebElement> apply(WebElement input) {
              return input.findElements(selector);
            }
          }).iterator();
    } else {
      webElements = driver().findElements(selector).iterator();
    }

    return filter.getFilter().apply(ImmutableList.copyOf(webElements));
  }
}
