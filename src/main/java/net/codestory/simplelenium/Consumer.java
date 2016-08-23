package net.codestory.simplelenium;

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
