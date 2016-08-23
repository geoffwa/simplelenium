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
package net.codestory.simplelenium;

import com.google.common.base.Function;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;

public abstract class DomElement extends Navigation {

  // Nested find
  public abstract DomElement find(String selector);

  public abstract DomElement find(By selector);

  // Narrow find
  public abstract FilteredDomElement withText();

  public abstract FilteredDomElement withId();

  public abstract FilteredDomElement withName();

  public abstract FilteredDomElement withTagName();

  public abstract FilteredDomElement withClass();

  public abstract FilteredDomElement with(String name);

  public abstract FilteredDomElement withCss(String name);

  public abstract FilteredDomElement with(String description, Function<WebElement, String> toValue);

  // Limit results

  public abstract DomElement first();

  public abstract DomElement second();

  public abstract DomElement third();

  public abstract DomElement nth(int index);

  public abstract DomElement limit(int max);

  public abstract DomElement skip(int count);

  public abstract DomElement last();

  public abstract DomElement filter(String description, Function<Iterable<WebElement>, Iterable<WebElement>> filter);

  // Shortcuts

  public DomElement withText(String text) {
    return withText().containing(text);
  }

  public DomElement withId(String id) {
    return withId().equalTo(id);
  }

  public DomElement withName(String name) {
    return withName().equalTo(name);
  }

  public DomElement withClass(String cssClass) {
    return withClass().containingWord(cssClass);
  }

  public DomElement withTagName(String name) {
    return withTagName().equalTo(name);
  }

  // Assertions

  public abstract Should should();

  // Actions

  public abstract DomElement fill(CharSequence text);

  public abstract DomElement append(CharSequence text);

  public abstract DomElement pressReturn();

  public abstract DomElement pressEnter();

  public abstract DomElement sendKeys(CharSequence... keysToSend);

  public abstract DomElement clear();

  public abstract DomElement submit();

  public abstract DomElement check();

  public abstract DomElement uncheck();

  public abstract DomElement click();

  public abstract DomElement click(int x, int y);

  public abstract DomElement doubleClick();

  public abstract DomElement doubleClick(int x, int y);

  public abstract DomElement clickAndHold();

  public abstract DomElement dragAndDropTo(String selector);

  public abstract DomElement contextClick();

  public abstract DomElement release();

  public abstract DomElement executeActions(String description, BiConsumer<WebElement, Actions> actionsOnElement);

  // Selection

  public abstract DomElement select(String text);

  public abstract DomElement deselect();

  public abstract DomElement deselectByValue(String value);

  public abstract DomElement deselectByVisibleText(String text);

  public abstract DomElement deselectByIndex(int index);

  public abstract DomElement selectByIndex(int index);

  public abstract DomElement selectByValue(String value);

  public abstract DomElement executeSelect(String description, Consumer<Select> selectOnElement);

  // Actions on low level elements

  public abstract DomElement execute(Consumer<WebElement> action);

  // Retry

  public abstract DomElement retryFor(long duration, TimeUnit timeUnit);
}
