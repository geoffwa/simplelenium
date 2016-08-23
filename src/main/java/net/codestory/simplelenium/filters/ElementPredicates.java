package net.codestory.simplelenium.filters;


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
