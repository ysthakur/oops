package io.github.ysthakur.oops;

import lombok.NonNull;
import org.jetbrains.annotations.NotNull;

public class Strings {
  public static Value from(char c) {
    return Nats.fromInt(c);
  }

  public static Value from(@NonNull String str) {
    var res = Lists.NIL;
    for (int i = str.length() - 1; i >= 0; i--) {
      res = Lists.cons(from(str.charAt(i)), res);
    }
    return res;
  }

  @NotNull
  public static String toJavaStr(@NonNull Value str) {
    var chars = Lists.toJavaList(str);
    var res = new StringBuilder();
    for (var c : chars) {
      res.append((char) Nats.toInt(c));
    }
    return res.toString();
  }
}
