package io.github.ysthakur.oops;

import io.github.ysthakur.oops.parse.Span;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

@Accessors(fluent = true)
public final class Value {
  private final Map<String, Value> fields;
  @Getter
  private final Span span;

  public Value(Map<String, Value> fields) {
    this(fields, Span.GARBAGE);
  }

  public Value(Map<String, Value> fields, Span span) {
    this.fields = fields;
    this.span = span;
  }

  public Value get(String fieldName) {
    return this.fields.get(fieldName);
  }

  public boolean hasField(String fieldName) {
    return this.fields.containsKey(fieldName);
  }

  public Map<String, Value> fields() {
    return new HashMap<>(this.fields);
  }

  /**
   * Make a new value with the given field
   */
  public Value withField(String fieldName, Value value) {
    var newFields = new HashMap<>(this.fields);
    newFields.put(fieldName, value);
    return new Value(newFields, this.span);
  }

  public Value withSpan(Span span) {
    return new Value(Map.copyOf(this.fields), span);
  }

  @Override
  public String toString() {
    var type = Type.guessType(this);
    if (type == Type.Named.Empty) {
      return "{}";
    } else if (type == Type.Named.Nat) {
      return Integer.toString(Nats.toInt(this));
    } else if (type instanceof Type.Lst listType) {
      var elemType = listType.elem();
      if (elemType == Type.Named.Empty || elemType == Type.Named.Nat) {
        // A string
        // todo escape quotes
        return '"' + Strings.toJavaStr(this) + '"';
      } else {
        // A list
        var elems = Lists.toJavaList(this);
        var res = new StringBuilder();
        for (var elem : elems) {
          res.append("{cons ");
          res.append(elem.toString());
          res.append(' ');
        }
        res.append("{}");
        res.append("}".repeat(elems.size()));
        return res.toString();
      }
    } else {
      // todo implement
      return type + "{new" + fields + "}";
    }
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o instanceof Value v) {
      if (!this.fields.keySet().equals(v.fields.keySet())) {
        return false;
      }
      for (var field : this.fields.keySet()) {
        if (!this.fields.get(field).equals(v.fields.get(field))) {
          return false;
        }
      }
      return true;
    }
    return false;
  }
}
