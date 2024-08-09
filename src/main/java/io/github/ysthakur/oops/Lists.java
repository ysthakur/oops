package io.github.ysthakur.oops;

import io.github.ysthakur.oops.eval.EvalError;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class Lists {
    public static final Value NIL;

    static {
        var nilFields = new HashMap<String, Value>();
        NIL = new Value(nilFields);
        nilFields.put("ifTrue", Methods.from(List.of(Strings.from(""), Strings.from("")), null));
    }

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

    public static boolean isCons(@NotNull Value value) {
        return value.hasField("car") && value.hasField("cdr");
    }

    @NotNull
    public static Value from(Value @NotNull ... values) {
        var res = NIL;
        for (int i = values.length - 1; i >= 0; i--) {
            res = cons(values[i], res);
        }
        return res;
    }

    @NotNull
    public static Value from(@NotNull List<Value> values) {
        return from(values.toArray(new Value[0]));
    }

    public static @NotNull List<Value> toJavaList(@NotNull Value list) {
        var res = new ArrayList<Value>();
        while (!list.equals(NIL)) {
            if (!isCons(list)) {
                throw new EvalError("Not a cons: " + list, list.span());
            }
            res.add(car(list));
            list = cdr(list);
        }
        return res;
    }
}
