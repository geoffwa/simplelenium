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

import static org.assertj.core.api.Assertions.assertThat;

public class ClickTest extends AbstractTest {
  @Before
  public void goToIndex() {
    goTo("/");
  }

  @Test
  public void click() {
    find("a").click();

    assertThat(url()).isEqualTo(getDefaultBaseUrl() + "/");
    assertThat(path()).isEqualTo("/");
  }

  @Test
  public void click_with_text() {
    find("a").withText("First Link").click();

    assertThat(url()).isEqualTo(getDefaultBaseUrl() + "/");
    assertThat(path()).isEqualTo("/");

    find("a").withText("Second Link").click();

    assertThat(url()).isEqualTo(getDefaultBaseUrl() + "/list");
    assertThat(path()).isEqualTo("/list");
  }
}
