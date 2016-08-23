/**
 * Copyright (C) 2013-2015 all@code-story.net
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package net.codestory.simplelenium;

import com.google.common.base.Predicate;
import org.openqa.selenium.WebElement;

import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public abstract class Should extends Navigation {
  // Modifiers

  public abstract Should within(long duration, TimeUnit timeUnit);

  public abstract Should not();

  // Expectations

  public abstract ShouldChain contain(String... texts);

  public abstract ShouldChain beEmpty();

  public abstract ShouldChain match(Pattern regexp);

  public abstract ShouldChain beEnabled();

  public abstract ShouldChain beDisplayed();

  public abstract ShouldChain beSelected();

  public abstract ShouldChain haveLessItemsThan(int maxCount);

  public abstract ShouldChain haveSize(int size);

  public abstract ShouldChain haveMoreItemsThan(int minCount);

  public abstract ShouldChain exist();

  public abstract ShouldChain haveDimension(int width, int height);

  public abstract ShouldChain beAtLocation(int x, int y);

  public abstract ShouldChain match(Predicate<WebElement> condition);
}
