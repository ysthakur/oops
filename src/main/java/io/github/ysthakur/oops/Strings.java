package io.github.ysthakur.oops;

import org.jetbrains.annotations.NotNull;

public class Strings {
  public static Value fromChar(char c) {
    return Nats.fromInt(c);
  }

  public static Value fromJavaStr(@NotNull String str) {
    var res = Lists.NIL;
    for (char c : str.toCharArray()) {
      res = Lists.cons(fromChar(c), res);
    }
    return res;
  }
}
