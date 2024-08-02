package io.github.ysthakur.oops.eval;

import io.github.ysthakur.oops.*;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class Eval {
  private static final String SPREAD_OP = "...";

  private final Map<String, Value> vars = new HashMap<>();

  @Nullable
  Value getVar(String name) {
    System.out.println("Vars: " + this.vars);
    return vars.get(name);
  }

  public Value eval(Value expr) {
    var type = Type.guessType(expr);
    if (!(type instanceof Type.Lst listType)) {
      throw new EvalError("Cannot evaluate " + expr, expr.span());
    }

    if (listType.elem().isNat()) {
      // The value is a string, so it's a variable reference
      return vars.getOrDefault(Strings.toJavaStr(expr), Lists.NIL);
    }

    var cmd = Strings.toJavaStr(Lists.car(expr));
    var args = Lists.toJavaList(Lists.cdr(expr));

    switch (cmd) {
      case "$" -> {
        if (args.size() != 1) {
          throw new EvalError("Expected 1 argument", expr.span());
        }
        return eval(eval(args.get(0)));
      }
      case "'" -> {
        if (args.size() != 1) {
          throw new EvalError("Expected 1 argument", expr.span());
        }
        return args.get(0);
      }
      case "car" -> {
        if (args.size() != 1) {
          throw new EvalError("Expected 1 argument", expr.span());
        }
        return Lists.car(eval(args.get(0)));
      }
      case "cdr" -> {
        if (args.size() != 1) {
          throw new EvalError("Expected 1 argument", expr.span());
        }
        return Lists.cdr(eval(args.get(0)));
      }
      case "cons" -> {
        if (args.isEmpty()) {
          return Lists.NIL;
        }
        var res = eval(args.get(args.size() - 1));
        for (int i = args.size() - 2; i >= 0; i--) {
          res = Lists.cons(eval(args.get(i)), res);
        }
        return res;
      }
      case "let" -> {
        if (args.isEmpty()) {
          throw new EvalError("No arguments given to let", expr.span());
        }
        var decls = args.get(0);
        if (!Type.guessType(decls).subtypeOf(new Type.Lst(new Type.Lst(Type.Any)))) {
          throw new EvalError("Expected a list of variable declarations", decls.span());
        }

        for (var decl : Lists.toJavaList(decls)) {
          var declParts = Lists.toJavaList(decl);
          if (declParts.size() != 2) {
            throw new EvalError("Expected a variable name and a value", decl.span());
          }

          var name = declParts.get(0);
          if (!Type.guessType(name).subtypeOf(Type.Str)) {
            throw new EvalError("Variable name should be a string", name.span());
          }

          var value = eval(declParts.get(1));
          this.vars.put(Strings.toJavaStr(name), value);
          System.out.println("Just put " + Strings.toJavaStr(name) + ", value: " + value);
        }

        for (var i = 1; i < args.size() - 1; i++) {
          System.out.println("Evaluating " + args.get(i));
          eval(args.get(i));
        }

        System.out.println("Vars: " + this.vars);
        System.out.println("Will evaluate " + args.get(args.size() - 1));
        return eval(args.get(args.size() - 1));
      }
      case "new" -> {
        var fields = new HashMap<String, Value>();
        for (var field : args) {
          var parts = Lists.toJavaList(field);
          if (parts.size() != 2 && parts.size() != 3) {
            throw new EvalError("Expected a field name and value or a method name, " +
                "param list, and body", field.span());
          }
          var name = Strings.toJavaStr(parts.get(0));
          if (parts.size() == 3) {
            var params = Lists.toJavaList(parts.get(1));
            var body = parts.get(2);
            fields.put(name, Methods.from(params, body));
          } else if (name.equals(SPREAD_OP)) {
            var obj = eval(parts.get(1));
            fields.putAll(obj.fields());
          } else {
            fields.put(name, eval(parts.get(1)));
          }
        }
        return new Value(fields);
      }
      default -> {
        if (args.isEmpty()) {
          throw new EvalError("Expected an object to get the field from", expr.span());
        }
        var obj = eval(args.get(0));
        var field = obj.get(cmd);
        if (field == null) {
          throw new EvalError("No field " + cmd + " in " + obj, expr.span());
        }
        if (args.size() == 1) {
          // Field access
          return eval(field);
        } else {
          // Method call
          var params = Methods.params(field);
          if (args.size() - 1 != params.size()) {
            throw new EvalError("Expected " + params.size() + " arguments but got " + (args.size() - 1), expr.span());
          }
          for (var i = 0; i < params.size(); i++) {
            this.vars.put(params.get(i), args.get(i + 1));
          }
          return eval(Methods.body(field));
        }
      }
    }
  }
}
