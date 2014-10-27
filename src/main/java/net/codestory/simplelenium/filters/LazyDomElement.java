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
package net.codestory.simplelenium.filters;

import net.codestory.simplelenium.DomElement;
import net.codestory.simplelenium.Should;
import net.codestory.simplelenium.driver.CurrentWebDriver;
import net.codestory.simplelenium.text.Text;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.Select;

import java.util.concurrent.TimeUnit;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;
import java.util.stream.Stream;

public class LazyDomElement implements DomElement {
  private final By selector;
  private final ElementFilter filter;
  private final Retry retry;

  public LazyDomElement(By selector) {
    this(selector, Retry._30_SECONDS);
  }

  public LazyDomElement(By selector, Retry retry) {
    this(selector, ElementFilter.any(), retry);
  }

  private LazyDomElement(By selector, ElementFilter filter, Retry retry) {
    this.selector = selector;
    this.filter = filter;
    this.retry = retry;
  }

  // Narrow find

  @Override
  public ElementFilterBuilder withText() {
    return narrow("text", element -> element.getText());
  }

  @Override
  public ElementFilterBuilder withId() {
    return narrow("id", element -> element.getAttribute("id"));
  }

  @Override
  public ElementFilterBuilder withName() {
    return narrow("id", element -> element.getAttribute("name"));
  }

  @Override
  public ElementFilterBuilder withTagName() {
    return narrow("tag name", element -> element.getTagName());
  }

  @Override
  public ElementFilterBuilder withClass() {
    return narrow("class", element -> element.getAttribute("class"));
  }

  @Override
  public ElementFilterBuilder withAttribute(String name) {
    return narrow("attribute[" + name + "]", element -> element.getAttribute(name));
  }

  @Override
  public ElementFilterBuilder withCssValue(String name) {
    return narrow("cssValue[" + name + "]", element -> element.getCssValue(name));
  }

  private ElementFilterBuilder narrow(String description, Function<WebElement, String> toValue) {
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

  private LazyDomElement filter(String description, UnaryOperator<Stream<WebElement>> filter) {
    return with(new ElementFilter(", " + description, filter));
  }

  LazyDomElement with(ElementFilter filter) {
    return new LazyDomElement(selector, this.filter.and(filter), retry);
  }


  // Assertions

  @Override
  public Should should() {
    return new LazyShould(selector, filter, Retry._5_SECONDS, false);
  }

  // Actions

  @Override
  public void fill(CharSequence text) {
    execute("fill(" + text + ")", element -> element.sendKeys(text));
  }

  @Override
  public void pressReturn() {
    execute("pressReturn()", element -> element.sendKeys(Keys.RETURN));
  }

  @Override
  public void sendKeys(CharSequence... keysToSend) {
    execute("sendKeys()", element -> element.sendKeys(keysToSend));
  }

  @Override
  public void clear() {
    execute("clear()", element -> element.clear());
  }

  @Override
  public void submit() {
    execute("submit", element -> element.submit());
  }

  @Override
  public void click() {
    execute("click", element -> element.click());
  }

  @Override
  public void doubleClick() {
    executeActions("doubleClick", (element, actions) -> actions.doubleClick(element));
  }

  @Override
  public void clickAndHold() {
    executeActions("clickAndHold", (element, actions) -> actions.clickAndHold(element));
  }

  @Override
  public void contextClick() {
    executeActions("contextClick", (element, actions) -> actions.contextClick(element));
  }

  @Override
  public void release() {
    executeActions("release", (element, actions) -> actions.release(element));
  }

  @Override
  public void executeActions(String description, BiConsumer<WebElement, Actions> actionsOnElement) {
    execute(description, element -> {
      Actions actions = new Actions(CurrentWebDriver.get());
      actionsOnElement.accept(element, actions);
      actions.perform();
    });
  }

  // Selection

  @Override
  public void select(String text) {
    executeSelect("select(" + text + ")", select -> select.selectByVisibleText(text));
  }

  @Override
  public void deselect() {
    executeSelect("deselect()", select -> select.deselectAll());
  }

  @Override
  public void deselectByValue(String value) {
    executeSelect("deselectByValue(" + value + ")", select -> select.deselectByValue(value));
  }

  @Override
  public void deselectByVisibleText(String text) {
    executeSelect("deselectByVisibleText(" + text + ")", select -> select.deselectByVisibleText(text));
  }

  @Override
  public void deselectByIndex(int index) {
    executeSelect("deselectByIndex(" + index + ")", select -> select.deselectByIndex(index));
  }

  @Override
  public void selectByIndex(int index) {
    executeSelect("selectByIndex(" + index + ")", select -> select.selectByIndex(index));
  }

  @Override
  public void selectByValue(String value) {
    executeSelect("selectByValue(" + value + ")", select -> select.selectByValue(value));
  }

  @Override
  public void executeSelect(String description, Consumer<Select> selectOnElement) {
    execute(description, element -> {
      Select select = new Select(element);
      selectOnElement.accept(select);
    });
  }

  // Actions on low level elements

  @Override
  public void execute(Consumer<? super WebElement> action) {
    execute("execute(" + action + ")", action);
  }

  // Retry

  @Override
  public LazyDomElement retryFor(long duration, TimeUnit timeUnit) {
    return new LazyDomElement(selector, this.filter.and(filter), new Retry(duration, timeUnit));
  }

  // Internal

  private void execute(String message, Consumer<? super WebElement> action) {
    System.out.println(" - " + Text.toString(selector) + filter.getDescription() + "." + message);

    retry.execute(() -> findOne(), action);
  }

  private WebElement findOne() {
    Stream<WebElement> webElements = CurrentWebDriver.get().findElements(selector).stream();
    Stream<WebElement> filtered = filter.getFilter().apply(webElements);
    return filtered.findFirst().orElse(null);
  }
}