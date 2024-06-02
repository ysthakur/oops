package io.github.ysthakur.oops;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public final class Lists {
  public static Value NIL = new Value(Map.of());

  private Lists() {
  }

  /**
   * Cons two values together
   */
  @Contract("_, _ -> new")
  public static @NotNull Value cons(Value car, Value cdr) {
    return new Value(Map.of("car", car, "cdr", cdr));
  }
}
