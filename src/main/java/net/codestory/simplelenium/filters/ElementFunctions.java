package net.codestory.simplelenium.filters;

import com.google.common.base.Function;
import org.openqa.selenium.WebElement;

/**
 * Created by geoffwa on 20/08/2016.
 */
class ElementFunctions {
  private ElementFunctions() {
  }

  static final Function<WebElement, String> DIMENSION = new Function<WebElement, String>() {
    @Override
    public String apply(WebElement webElement) {
      return webElement.getSize().toString();
    }
  };

  static final Function<WebElement, String> LOCATION = new Function<WebElement, String>() {
    @Override
    public String apply(WebElement webElement) {
      return webElement.getLocation().toString();
    }
  };

}
