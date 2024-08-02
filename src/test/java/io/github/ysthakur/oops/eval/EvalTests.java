package io.github.ysthakur.oops.eval;

import io.github.ysthakur.oops.Lists;
import io.github.ysthakur.oops.Methods;
import io.github.ysthakur.oops.Strings;
import io.github.ysthakur.oops.Value;
import io.github.ysthakur.oops.parse.Parser;
import io.github.ysthakur.oops.parse.SourceFile;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

public class EvalTests {
  Value parse(String code) {
    return Parser.parse(new SourceFile("test", code));
  }

  Value eval(String code) {
    return new Eval().eval(parse(code));
  }

  void assertEq(Value expected, Value actual) {
    var expectedFieldsDiff = new ArrayList<String>();
    var actualFieldsDiff = new ArrayList<String>();
    for (var field : expected.fields().keySet()) {
      if (!actual.hasField(field)) {
        expectedFieldsDiff.add(field);
      } else if (!Objects.equals(expected.get(field), actual.get(field))) {
        expectedFieldsDiff.add(field);
        actualFieldsDiff.add(field);
      }
    }
    for (var field : actual.fields().keySet()) {
      if (!expected.hasField(field)) {
        actualFieldsDiff.add(field);
      }
    }
    if (!expectedFieldsDiff.isEmpty() || !actualFieldsDiff.isEmpty()) {
      fail("Expected {" + expectedFieldsDiff.stream().map(field -> field + ": " + expected.get(field)).collect(Collectors.joining(", ")) + "}, got {" + actualFieldsDiff.stream().map(field -> field + ": " + actual.get(field)).collect(Collectors.joining(", ")) + "}");
    }
  }

  @Test
  void quoteEmpty() {
    assertEq(new Value(Map.of()), eval("{'{}}"));
  }

  @Test
  void quoteAtom() {
    assertEq(Strings.from("foo"), eval("{' foo}"));
  }

  @Test
  void quoteList() {
    assertEq(Lists.from(Strings.from("foo"), Strings.from("bar"), Lists.NIL), eval("{' " +
        "{foo bar {}}}"));
  }

  @Test
  void car() {
    assertEq(Strings.from("foo"), eval("{car {cons \"foo\" \"bar\"}}"));
  }

  @Test
  void cdr() {
    assertEq(Strings.from("bar"), eval("{cdr {cons \"foo\" \"bar\"}}"));
  }

  @Test
  void cons() {
    assertEq(Lists.from(Strings.from("foo"), Strings.from("bar")), eval("{cons \"foo\" " +
        "\"bar\" {' {}}}"));
  }

  @Test
  void letMultipleExpressions() {
    var eval = new Eval();
    var res = eval("""
        {let
          {{fooVar "foo"}}
          {let
            {{barVar "bar"}}
            {cons fooVar barVar}}}""");
    assertEquals(Strings.from("foo"), eval.getVar("fooVar"));
    assertEquals(Strings.from("bar"), eval.getVar("barVar"));
    assertEq(Lists.from(Strings.from("foo"), Strings.from("bar")), res);
  }

  @Test
  void newObject() {
    assertEq(new Value(Map.of("foo", Strings.from("bar"), "baz",
        Methods.from(List.of(Strings.from("bleh")), new Value(Map.of())))), eval("""
        {new
          {foo {' bar}}
          {baz {bleh} {}}}"""));
  }

  @Test
  void newWithSpread() {
    // todo test order too
    assertEq(
        new Value(Map.of("foo", Strings.from("bar"))),
        eval("""
            {new
              {foo "bleh"}
              {... {new
                     {foo "bar"}}}}"""));
  }

  @Test
  void methodCall() {
    assertEq(Strings.from("helloworld"), eval("""
        {baz
          {new
            {foo {' bar}}
            {baz {bleh} {$ bleh}}}
          {' helloworld}}"""));
  }

  @Test
  void subst() {
    assertEq(Strings.from("helloworld"), eval("""
        {baz
          {new
            {foo {' bar}}
            {baz {bleh} {$ bleh}}}
          {' helloworld}}"""));
  }
}
