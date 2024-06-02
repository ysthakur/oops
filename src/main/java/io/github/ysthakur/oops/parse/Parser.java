package io.github.ysthakur.oops.parse;

import io.github.ysthakur.oops.Lists;
import io.github.ysthakur.oops.Strings;
import io.github.ysthakur.oops.Value;
import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

public class Parser {
  private final SourceFile source;
  private final String code;

  /**
   * Where in the code we currently are
   */
  private int offset = 0;

  private Parser(SourceFile source) {
    this.source = source;
    this.code = source.text;
  }

  public static Result parse(SourceFile file) {
    return new Parser(file).parse();
  }

  private char curr() {
    return this.code.charAt(this.offset);
  }

  private Result parse() {
    trimWhitespace();
    if (this.offset == code.length()) {
      return unexpectedEof();
    }

    if (curr() == '{') {
      this.offset++;
      return parseSexpr();
    } else {
      return parseAtom();
    }
  }

  private Result parseSexpr() {
    trimWhitespace();
    if (this.offset == code.length()) {
      return unexpectedEof();
    }

    if (curr() == '}') {
      return new Result.Ok(Lists.NIL);
    }

    return parse().flatMap((atom) -> parseSexpr().flatMap(rest -> new Result.Ok(Lists.cons(atom, rest))));
  }

  private @NotNull Result parseAtom() {
    trimWhitespace();
    if (this.offset == code.length()) {
      return unexpectedEof();
    }

    if (curr() == '{' || curr() == '}') {
      // This isn't a Result.Err because it should never happen
      throw new AssertionError("Expected atom, got curly brace (" + curr() + ")");
    }

    var start = this.offset;
    while (this.offset < code.length() && !Character.isWhitespace(curr()) && curr() != '{' && curr() != '}') {
      this.offset++;
    }

    return new Result.Ok(Strings.fromJavaStr(code.substring(start, this.offset)));
  }

  private void trimWhitespace() {
    while (this.offset < code.length() && Character.isWhitespace(curr())) {
      this.offset++;
    }
  }

  private Result unexpectedEof() {
    return new Result.Err("Unexpected EOF", new Span(this.offset, this.offset, this.source));
  }

  public sealed interface Result permits Result.Ok, Result.Err {
    boolean isOk();

    Result flatMap(Function<Value, Result> f);

    record Ok(Value parsed) implements Result {
      @Override
      public boolean isOk() {
        return true;
      }

      @Override
      public Result flatMap(Function<Value, Result> f) {
        return f.apply(this.parsed);
      }
    }

    record Err(String msg, Span location) implements Result {
      @Override
      public boolean isOk() {
        return false;
      }

      @Override
      public Result flatMap(Function<Value, Result> f) {
        return this;
      }
    }
  }
}
