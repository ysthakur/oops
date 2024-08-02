package io.github.ysthakur.oops;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Lists {
  public static Value NIL = new Value(Map.of());

  private Lists() {
  }

  public static Value car(Value list) {
    var car = list.get("car");
    if (car == null) {
      throw new RuntimeException("Not a list: " + list);
    }
    return car;
  }

  public static Value cdr(Value list) {
    return list.get("cdr");
  }

  /**
   * Cons two values together
   */
  @Contract("_, _ -> new")
  public static @NotNull Value cons(Value car, Value cdr) {
    return new Value(Map.of("car", car, "cdr", cdr));
  }

  public static Value from(Value @NotNull ... values) {
    var res = NIL;
    for (int i = values.length - 1; i >= 0; i--) {
      res = cons(values[i], res);
    }
    return res;
  }

  public static Value from(List<Value> values) {
    return from(values.toArray(new Value[0]));
  }

  public static List<Value> toJavaList(Value list) {
    var res = new ArrayList<Value>();
    while (list != NIL) {
      res.add(car(list));
      list = cdr(list);
    }
    return res;
  }
}
