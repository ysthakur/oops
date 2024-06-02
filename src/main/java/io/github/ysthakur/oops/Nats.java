package io.github.ysthakur.oops;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Nats {
  public static Value ZERO = new Value(Map.of());

  @Contract("_ -> new")
  public static @NotNull Value succ(Value prev) {
    return new Value(Map.of("prev", prev));
  }

  public static Value fromInt(int i) {
    if (i < 0) {
      throw new IllegalArgumentException("Only non-negative integers can be represented, got " + i);
    } else if (i == 0) {
      return ZERO;
    } else {
      return succ(fromInt(i - 1));
    }
  }
}
