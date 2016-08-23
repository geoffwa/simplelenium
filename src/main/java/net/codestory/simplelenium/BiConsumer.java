package net.codestory.simplelenium;

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

