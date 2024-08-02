package io.github.ysthakur.oops;

import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public class Methods {
  private static final String PARAMS_FIELD = "params";
  private static final String BODY_FIELD = "body";

  public static Value from(List<Value> params, Value body) {
    return new Value(Map.of(PARAMS_FIELD, Lists.from(params), BODY_FIELD, body));
  }

  public static List<String> params(@NotNull Value method) {
    return Lists.toJavaList(method.get(PARAMS_FIELD)).stream().map(Strings::toJavaStr).toList();
  }

  public static Value body(Value method) {
    return method.get(BODY_FIELD);
  }
}
