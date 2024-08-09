package io.github.ysthakur.oops;

import io.github.ysthakur.oops.eval.EvalError;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class Nats {
  public static Value ZERO = Lists.NIL;

  @Contract("_ -> new")
  public static @NotNull Value succ(Value prev) {
    return new Value(Map.of("prev", prev));
  }

  public static Value fromInt(int i) {
    if (i < 0) {
      throw new IllegalArgumentException("Only non-negative integers can be " +
          "represented, got " + i);
    }
    var res = ZERO;
    while (i > 0) {
      res = succ(res);
      i --;
    }
    return res;
  }

  public static int toInt(@NotNull Value nat) {
    int res = 0;
    var curr = nat;
    while (curr != Nats.ZERO) {
      curr = curr.get("prev");
      if (curr == null) {
        throw new EvalError("Not a number: " + nat, nat.span());
      }
      res++;
    }
    return res;
  }
}
