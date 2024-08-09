package io.github.ysthakur.oops;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;

public class Methods {
  private static final String PARAMS_FIELD = "params";
  private static final String BODY_FIELD = "body";

  @Contract("_, _ -> new")
  public static @NotNull Value from(List<Value> params, @Nullable Value body) {
    var fields = new HashMap<String, Value>();
    fields.put(PARAMS_FIELD, Lists.from(params));
    if (body != null) {
      fields.put(BODY_FIELD, body);
    }
    return new Value(fields);
  }

  public static List<String> params(@NotNull Value method) {
    return Lists.toJavaList(method.get(PARAMS_FIELD)).stream().map(Strings::toJavaStr).toList();
  }

  @NotNull public static Value body(@NotNull Value method) {
    return method.fields().getOrDefault(BODY_FIELD, Lists.from(Strings.from("'"), Lists.NIL));
  }
}
