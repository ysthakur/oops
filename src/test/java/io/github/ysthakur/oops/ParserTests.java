package io.github.ysthakur.oops;

import io.github.ysthakur.oops.parse.Parser;
import io.github.ysthakur.oops.parse.SourceFile;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTests {
  Value parse(String code) {
    return Parser.parse(new SourceFile("test", code));
  }

  Parser.ParseError parseErr(String code) {
    return assertThrows(Parser.ParseError.class, () -> parse(code));
  }

  @Test
  void unclosed() {
    var err = parseErr("{ foo");
    assertTrue(err.msg().contains("Unexpected EOF"), err.toString());
    assertEquals(5, err.location().getStart());
    assertEquals(5, err.location().getEnd());
  }

  @Test
  void extraStuff() {
    var err = parseErr("{foo}}asdf");
    assertTrue(err.msg().contains("Extra stuff"), err.toString());
    assertEquals(5, err.location().getStart());
    assertEquals(10, err.location().getEnd());
  }

  @Test
  void atom() {
    assertEquals(Strings.from("foo"), parse("foo"));
  }

  @Test
  void string() {
    assertEquals(parse("{' foo}"), parse("\"foo\""));
  }

  @Test
  void op() {
    assertEquals(
        Lists.from(Strings.from("cons"), parse("\"foo\""), parse("\"bar\"")),
        parse("{cons \"foo\" \"bar\"}"));
  }

  @Test
  void flatList() {
    var parsed = parse("{ foo . bar ??? }");
    assertEquals(
        Lists.from(
            Strings.from("foo"),
            Strings.from("."),
            Strings.from("bar"),
            Strings.from("???")),
        parsed);
  }

  @Test
  void nestedList() {
    var parsed = parse("{ foo { . { '} bar } ??? }");
    assertEquals(
        Lists.from(
            Strings.from("foo"),
            Lists.from(
                Strings.from("."),
                Lists.from(Strings.from("'")),
                Strings.from("bar")),
            Strings.from("???")),
        parsed);
  }
}
