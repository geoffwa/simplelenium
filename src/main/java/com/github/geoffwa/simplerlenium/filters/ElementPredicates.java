/**
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
package com.github.geoffwa.simplerlenium.filters;

import com.google.common.base.Predicate;
import org.openqa.selenium.Point;
import org.openqa.selenium.WebElement;

class ElementPredicates {

  private ElementPredicates() {
  }

  static final Predicate<WebElement> IS_ENABLED = new Predicate<WebElement>() {
    @Override
    public boolean apply(WebElement webElement) {
      return webElement.isEnabled();
    }
  };

  static final Predicate<WebElement> IS_DISPLAYED = new Predicate<WebElement>() {
    @Override
    public boolean apply(WebElement webElement) {
      return webElement.isDisplayed();
    }
  };

  static final Predicate<WebElement> IS_SELECTED = new Predicate<WebElement>() {
    @Override
    public boolean apply(WebElement webElement) {
      return IS_SELECTABLE.apply(webElement) && webElement.isSelected();
    }
  };

  static final Predicate<WebElement> IS_SELECTABLE = new Predicate<WebElement>() {
    @Override
    public boolean apply(WebElement webElement) {
      return webElement.getTagName().equals("input") || webElement.getTagName().equals("option");
    }
  };

  static Predicate<WebElement> HAS_LOCATION(final int x, final int y) {
    return new Predicate<WebElement>() {
      @Override
      public boolean apply(WebElement webElement) {
        Point location = webElement.getLocation();
        return location.getX() == x && location.getY() == y;
      }
    };
  }

}
