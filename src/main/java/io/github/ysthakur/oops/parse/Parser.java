package io.github.ysthakur.oops.parse;

import io.github.ysthakur.oops.Lists;
import io.github.ysthakur.oops.Strings;
import io.github.ysthakur.oops.Value;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.NotNull;

public class Parser {
  private final SourceFile source;
  private final String code;

  /**
   * Where in the code we currently are
   */
  private int offset = 0;

  private Parser(SourceFile source) {
    this.source = source;
    this.code = source.getText();
  }

  public static Value parse(SourceFile file) {
    return new Parser(file).parseFull();
  }

  private char curr() {
    return this.code.charAt(this.offset);
  }

  private Value parseFull() {
    var res = parse();
    if (this.offset != code.length()) {
      throw new ParseError("Extra stuff", new Span(this.offset, code.length(),
          this.source));
    } else {
      return res;
    }
  }

  private Value parse() {
    trimWhitespace();
    if (this.offset == code.length()) {
      unexpectedEof();
    }

    if (curr() == '}') {
      throw new ParseError("Unexpected closing brace", new Span(this.offset,
          this.offset + 1, this.source));
    } else if (curr() == '{') {
      this.offset++;
      return parseSexpr();
    } else {
      return parseAtom();
    }
  }

  private Value parseSexpr() {
    trimWhitespace();
    if (this.offset == code.length()) {
      unexpectedEof();
    }

    if (curr() == '}') {
      this.offset++;
      return Lists.NIL;
    }

    var atom = parse();
    var rest = parseSexpr();
    return Lists.cons(atom, rest);
  }

  private @NotNull Value parseAtom() {
    trimWhitespace();
    if (this.offset == code.length()) {
      unexpectedEof();
    }

    if (curr() == '{' || curr() == '}') {
      // This isn't a ParseError because it should never happen
      throw new AssertionError("Expected atom, got curly brace (" + curr() + ")");
    }

    var start = this.offset;
    if (curr() == '"') {
      this.offset++;
      return parseString();
    }
    while (this.offset < code.length() && !Character.isWhitespace(curr()) && curr() != '{' && curr() != '}') {
      this.offset++;
    }

    return Strings.from(code.substring(start, this.offset))
        .withSpan(new Span(start, this.offset, source));
  }

  private @NotNull Value parseString() {
    var start = this.offset;
    var res = new StringBuilder();
    while (this.offset < code.length() && curr() != '"') {
      res.append(curr());
      this.offset++;
    }
    if (this.offset == code.length()) {
      unexpectedEof();
    }
    var span = new Span(start, this.offset, source);
    this.offset++;
    // Wrap the string in a quote
    return Lists.from(Strings.from("'"), Strings.from(res.toString()).withSpan(span)).withSpan(span);
  }

  private void trimWhitespace() {
    while (this.offset < code.length() && Character.isWhitespace(curr())) {
      this.offset++;
    }
  }

  private void unexpectedEof() {
    throw new ParseError("Unexpected EOF", new Span(this.offset, this.offset,
        this.source));
  }

  @AllArgsConstructor
  @Getter
  @Accessors(fluent = true)
  public static class ParseError extends RuntimeException {
    private final String msg;
    private final Span location;
  }
}
