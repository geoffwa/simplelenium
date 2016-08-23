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

public abstract class Consumer<T> {

  public abstract void accept(T t);

  public Consumer<T> andThen(final Consumer<? super T> after) {
    Objects.requireNonNull(after);
    return new AndThenConsumer<>(this, after);
  }

  static class AndThenConsumer<T> extends Consumer<T> {

    private final Consumer<T> first;
    private final Consumer<? super T> second;

    public AndThenConsumer(Consumer<T> first, Consumer<? super T> second) {
      this.first = first;
      this.second = second;
    }

    @Override
    public void accept(T t) {
      first.accept(t);
      second.accept(t);
    }
  }

}
