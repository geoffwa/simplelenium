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
package com.github.geoffwa.simplerlenium;

import java.util.Objects;

public abstract class BiConsumer<T, U> {

  public abstract void accept(T t, U u);

  public BiConsumer<T, U> andThen(final BiConsumer<? super T, ? super U> after) {
    Objects.requireNonNull(after);
    return new AndThenBiConsumer<>(this, after);
  }

  static class AndThenBiConsumer<T, U> extends BiConsumer<T, U> {

    private final BiConsumer<T, U> first;
    private final BiConsumer<? super T, ? super U> second;

    public AndThenBiConsumer(BiConsumer<T, U> first, BiConsumer<? super T, ? super U> second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public void accept(T t, U u) {
      first.accept(t, u);
      second.accept(t, u);
    }
  }

}

