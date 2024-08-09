package io.github.ysthakur.oops.eval;

import io.github.ysthakur.oops.*;
import io.github.ysthakur.oops.parse.Parser;
import io.github.ysthakur.oops.parse.SourceFile;
import io.github.ysthakur.oops.parse.Span;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Eval {
    private static final String SPREAD_OP = "...";

    private final Map<String, Value> vars = new HashMap<>();

    /**
     * These fields are shared across all objects. If an object doesn't have a particular
     * field, it's looked up here.
     */
    private final Map<String, Value> sharedFields = new HashMap<>();

    public Eval() {
        sharedFields.put("ifTrue", Methods.from(List.of(Strings.from(""), Strings.from("block")), Parser.parse(new SourceFile("foo", "{eval block}"))));
    }

    @Nullable
    Value getVar(String name) {
        System.out.println("Vars: " + this.vars);
        return vars.get(name);
    }

    public Value eval(Value expr) {
        System.out.println("Evaluating " + expr);
        System.out.println("Vars are " + vars);
        var type = Type.guessType(expr);
        if (!(type instanceof Type.Lst listType)) {
            throw new EvalError("Cannot evaluate " + expr, expr.span());
        }

        if (listType.elem().isNat()) {
            // The value is a string, so it's a variable reference
            return vars.getOrDefault(Strings.toJavaStr(expr), Lists.NIL);
        }

        var cmd = Strings.toJavaStr(Lists.car(expr));
        var cmdSpan = Lists.car(expr).span();
        var args = Lists.toJavaList(Lists.cdr(expr));

        if (args.isEmpty()) {
            throw new EvalError("No arguments provided", expr.span());
        }

        if (cmd.equals("eval")) {
            if (args.size() != 1) {
                throw new EvalError("Expected 1 argument", expr.span());
            }
            return eval(eval(args.get(0)));
        }
        if (cmd.equals("'")) {
            if (args.size() != 1) {
                throw new EvalError("Expected 1 argument", expr.span());
            }
            return args.get(0);
        }
        if (cmd.equals("new")) {
            var fields = new HashMap<String, Value>();
            for (var field : args) {
                var parts = Lists.toJavaList(field);
                if (parts.size() != 2 && parts.size() != 3) {
                    throw new EvalError("Expected a field name and value or a method name, " + "param list, and body", field.span());
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
        if (cmd.equals("let")) {
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

        var obj = eval(args.get(0));
        var fieldOrMethodCallRes = fieldOrMethodCall(cmd, obj, args.subList(1, args.size()), expr.span());
        if (fieldOrMethodCallRes.isPresent()) {
            return fieldOrMethodCallRes.get();
        }

        if (cmd.equals("cons")) {
            if (args.isEmpty()) {
                return Lists.NIL;
            }
            var res = eval(args.get(args.size() - 1));
            for (int i = args.size() - 2; i >= 0; i--) {
                res = Lists.cons(eval(args.get(i)), res);
            }
            return res;
        }

        throw new EvalError("Unrecognized command: " + cmd, cmdSpan);
    }

    /**
     * Access a field or call a method, if it exists
     *
     * @param fieldName The name of the field or method to get/call
     * @param obj       The object to whom the field belongs
     * @param args      The arguments
     * @param span      The span of the entire field access/method call expression
     * @return The field or method call result, or an empty Optional if the object doesn't have the field
     */
    private Optional<Value> fieldOrMethodCall(String fieldName, Value obj, List<Value> args, Span span) {
        var field = obj.get(fieldName);
        if (field == null) {
            field = sharedFields.get(fieldName);
        }
        if (field == null) {
            return Optional.empty();
        }
        if (args.isEmpty()) {
            // Field access
            return Optional.of(field);
        } else {
            // Method call
            var params = Methods.params(field);
            if (args.size() + 1 != params.size()) {
                throw new EvalError(
                        "Expected " + (params.size() - 1) + " arguments but got " + args.size() + " (method " + fieldName + ")", span);
            }
            this.vars.put(params.get(0), obj);
            for (var i = 0; i < args.size(); i++) {
                this.vars.put(params.get(i + 1), args.get(i));
            }
            return Optional.of(eval(Methods.body(field)));
        }
    }
}
