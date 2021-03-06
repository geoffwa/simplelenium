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
package com.github.geoffwa.simplerlenium;

import org.junit.Before;
import org.junit.Test;

public class DragAndDropTest extends AbstractTest {
  @Before
  public void goToDragAndDrop() {
    goTo("/dnd");
  }

  @Test
  public void before_drag_and_drop() throws InterruptedException {
    find("#left ul li").should().haveSize(3);
    find("#left ul").should().contain("Bob", "John", "Jane");

    find("#right ul li").should().haveSize(0);
  }

  @Test
  public void after_drag_and_drop() {
    find("#left ul li").withText("Bob").dragAndDropTo("#right");
    find("#left ul li").should().haveSize(2);
    find("#left ul").should().contain("John", "Jane");

  }

  @Test
  public void drag_all_items() {
    find("#left ul li").withText("Bob").dragAndDropTo("#right");
    find("#left ul li").withText("John").dragAndDropTo("#right");
    find("#left ul li").withText("Jane").dragAndDropTo("#right");

    find("#left ul li").should().haveSize(0);
    find("#right ul li").should().haveSize(3);
    find("#right ul").should().contain("Bob", "John", "Jane");
  }
}
